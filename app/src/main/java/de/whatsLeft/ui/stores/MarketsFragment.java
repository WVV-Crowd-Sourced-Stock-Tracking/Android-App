package de.whatsLeft.ui.stores;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.Objects;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;
import de.whatsLeft.connectivity.RequestStoresFromAPI;
import de.whatsLeft.store.Store;

/**
 * Fragment to display nearby stores and show their position on map
 * <p>Stores in listView are managed by the LVSAdapter</p>
 * @see LVSAdapter
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.1
 */
public class MarketsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "MarketsFragment";

    private GoogleMap mGoogleMap;
    private View mView;
    private ListView listViewStores;

    private boolean mapReady = false;

    private Location lastKnownLocation;

    private ArrayList<Store> stores = new ArrayList<>();
    private LVSAdapter adapter;

    private boolean upToDate;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_markets, container, false);
        listViewStores = mView.findViewById(R.id.store_list_view);

        // check if app has permission to access fine location of user
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if app has not the permission ask the user to grant it
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // setup card view adapter to process all available stores and display them as cards in the list view
        adapter = new LVSAdapter(getContext(), stores);
        listViewStores.setAdapter(adapter);

        // setup location manager to request user moving updates
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // setup google map view
        MapView mapView = mView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));

        // setup google maps view
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mapReady = true;

        // create on click listener for marker
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // get latitude and longitude from maker
                double latitude = marker.getPosition().latitude;
                double longitude = marker.getPosition().longitude;

                // find position for market at this location
                int position = adapter.getPosition(latitude, longitude);

                // scroll in list view to position of store
                listViewStores.smoothScrollToPositionFromTop(position, 20);
                return true;
            }
        });

        // set up to date to false every time the camera moved
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                upToDate = false;
            }
        });

        // if the camera moved request new stores based on the new camera location
        mGoogleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                if(!upToDate) {

                    // get camera position
                    CameraPosition cameraPosition = mGoogleMap.getCameraPosition();

                    // get latitude and longitude double value from camera position
                    double latitude = cameraPosition.target.latitude;
                    double longitude = cameraPosition.target.longitude;

                    // create new location object
                    Location location = new Location("");
                    location.setLatitude(latitude);
                    location.setLongitude(longitude);

                    // make toast to show user that app is loading new stores
                    Toast.makeText(getContext(), getString(R.string.loading_new_stores), Toast.LENGTH_LONG).show();

                    // request new stores
                    new RequestStoresFromAPI(getContext(), mGoogleMap, MainActivity.REQUEST_URL, location, stores).execute();

                    // inform the adapter that there might be changes in store list
                    adapter.notifyDataSetChanged();
                }
            }
        });

        updateCamera();
        new RequestStoresFromAPI(getContext(), mGoogleMap, MainActivity.REQUEST_URL, lastKnownLocation, stores).execute();

        // inform the adapter that there might be changes
        adapter.notifyDataSetChanged();
    }

    /**
     * Updates the camera's position
     *
     * @since 1.0.0
     */
    private void updateCamera() {

        // check if lastKnownLocation is null or not
        if (lastKnownLocation != null) {

            // set camera position
            LatLng lastKnownLocationLatLgn = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            CameraPosition position = new CameraPosition(lastKnownLocationLatLgn, 13f, 0f, 0f);

            // if map is ready move to camera to new position
            if (mapReady) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLocationChanged(Location location) {
        // update store lists
        lastKnownLocation = location;

        // if map is ready request new stores
        if(mapReady) new RequestStoresFromAPI(getContext(), mGoogleMap, MainActivity.REQUEST_URL, location, stores).execute();

        // inform the adapter that there might be new stores available
        adapter.notifyDataSetChanged();

        updateCamera();
        Log.d(TAG, "onLocationChanged: location: " + location.getLatitude() + "; " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.i(TAG, "onStatusChanged: gps status changed");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.i(TAG, "onProviderEnabled: GPS Enabled");
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.i(TAG, "onProviderDisabled: gps disabled");
    }

}
