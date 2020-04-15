package de.whatsLeft.ui.stores;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Objects;

import de.whatsLeft.R;
import de.whatsLeft.connectivity.RequestStoresAPI;
import de.whatsLeft.store.Store;

/**
 * Fragment to display nearby stores and show their position on map
 * <p>Stores in listView are managed by the LVSAdapter</p>
 * @see LVSAdapter
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.2
 */
public class MarketsFragment extends Fragment implements OnMapReadyCallback, LocationListener, ZipCodeDialogFragment.ZipCodeDialogListener {

    private static final String TAG = "MarketsFragment";

    private GoogleMap mGoogleMap;
    private View mView;
    private ListView listViewStores;

    private boolean mapReady = false;

    private Location lastKnownLocation;

    private ArrayList<Store> stores = new ArrayList<>();
    private LVSAdapter adapter;

    private String zipCode;

    private boolean locationAccess;
    private boolean firstRun = true;

    private ListView storesListView;
    private LinearLayout progressUpdate;
    private Button buttonEnterZipCode;


    private boolean upToDate = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_markets, container, false);
        listViewStores = mView.findViewById(R.id.store_list_view);

        // check if location access is granted
        locationAccess = ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // setup card view adapter to process all available stores and display them as cards in the list view
        adapter = new LVSAdapter(getContext(), stores);
        listViewStores.setAdapter(adapter);

        buttonEnterZipCode = mView.findViewById(R.id.button_enter_zip_code);

        // if location permission is granted check if gps is enabled
        if (locationAccess) {

            // setup location manager to request user moving updates
            LocationManager locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(Context.LOCATION_SERVICE);
            assert locationManager != null;

            // permission is granted now need to check whether gps is enabled
            locationAccess = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (locationAccess) getLastKnownLocation();
            Log.d(TAG, "onCreateView: lastKnownLocation: " + lastKnownLocation);
            Log.i(TAG, "onCreateView: provider: " + locationManager.getBestProvider(new Criteria(), true));
        }

        return mView;
    }

    /**
     * This method sets the lastKnownLocation field to last known location
     *
     * @since 1.0.0
     */
    private void getLastKnownLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(Objects.requireNonNull(getContext()));
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d(TAG, "onSuccess: location: " + location);
                lastKnownLocation = location;
                updateCamera();
                if (mapReady)
                    new RequestStoresAPI(getContext(), mGoogleMap, location, stores, adapter, storesListView, progressUpdate).execute();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressUpdate = view.findViewById(R.id.progress_bar_with_text);
        storesListView = view.findViewById(R.id.store_list_view);

        // setup google map view
        MapView mapView = mView.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        // if app cannot access location ask for zip code
        if (!locationAccess) {

            progressUpdate.setVisibility(View.GONE);
            buttonEnterZipCode.setVisibility(View.VISIBLE);
            buttonEnterZipCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new ZipCodeDialogFragment(MarketsFragment.this).show(getParentFragmentManager(), "ZipCodeDialog");
                }
            });

            DialogFragment dialog = new ZipCodeDialogFragment(this);
            dialog.show(getParentFragmentManager(), "ZipCodeDialog");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));

        // setup google maps view
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(locationAccess);
        mGoogleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mapReady = true;

        // create on click listener for marker
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                // display marker title
                marker.showInfoWindow();

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
                    if (!firstRun)
                        Toast.makeText(getContext(), getString(R.string.loading_new_stores), Toast.LENGTH_LONG).show();

                    // request new stores
                    if (lastKnownLocation != null && locationAccess)
                        new RequestStoresAPI(getContext(), mGoogleMap, location, stores, adapter, storesListView, progressUpdate).execute();
                    firstRun = false;
                }
            }
        });
        updateCamera();
    }

    /**
     * Updates the camera's position
     *
     * @since 1.0.0
     */
    private void updateCamera() {

        // check if lastKnownLocation is null or not
        if (lastKnownLocation != null && locationAccess) {

            // set camera position
            LatLng lastKnownLocationLatLgn = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            CameraPosition position = new CameraPosition(lastKnownLocationLatLgn, 13f, 0f, 0f);
            if (mapReady) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

            // if map is ready move to camera to new position
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLocationChanged(Location location) {
        // update store lists
        lastKnownLocation = location;

        // if map is ready request new stores
        if (mapReady && location != null)
            new RequestStoresAPI(getContext(), mGoogleMap, location, stores, adapter, listViewStores, progressUpdate).execute();

        updateCamera();
        assert location != null;
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

    @Override
    public void onDialogPositiveClick(DialogFragment dialogFragment) {
        EditText editText = Objects.requireNonNull(dialogFragment.getDialog()).findViewById(R.id.edit_text_zip_code);
        zipCode =  String.valueOf(editText.getText());

        // if progress bar is non visible make it visible
        if(progressUpdate.getVisibility() == View.GONE) {
            progressUpdate.setVisibility(View.VISIBLE);
            buttonEnterZipCode.setVisibility(View.GONE);
        }

        new RequestStoresAPI(getContext(), mGoogleMap, zipCode, stores, adapter, storesListView, progressUpdate).execute();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialogFragment) {

    }
}
