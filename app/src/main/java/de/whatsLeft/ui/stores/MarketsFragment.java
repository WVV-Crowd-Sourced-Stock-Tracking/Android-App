package de.whatsLeft.ui.stores;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;
import de.whatsLeft.connectivity.CallAPI;
import de.whatsLeft.store.Store;

/**
 * Fragment to display nearby stores and show their position on map
 * <p>Stores in listView are managed by the LVSAdapter</p>
 * @see LVSAdapter
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
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

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_markets, container, false);
        listViewStores = mView.findViewById(R.id.store_list_view);

        // check if app has permission to access fine location of user
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // if app has not the permission ask the user to grant it
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        // setup location manager to request user moving updates
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

        // setup card view adapter to process all available stores and display them as cards in the list view
        adapter = new LVSAdapter(getContext(), stores);
        listViewStores.setAdapter(adapter);


        return mView;
    }

    /**
     * Requests all available stores in a radius of 1000m around the current location from backend
     *
     * @param location Location; object which contains the current location of the user
     * @since 1.0.0
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void requestStores(Location location) {

        // create CallAPI object, to connect to backend
        CallAPI callAPI = new CallAPI();

        // create new String object to store result
        String result;

        // create new input json object for request
        JSONObject data = new JSONObject();
        try {
            // put all required information by the backend into input data json object
            data.put("latitude", String.valueOf(location.getLatitude()));
            data.put("longitude", String.valueOf(location.getLongitude()));
            data.put("radius", 1000);

            // connect to backend and store result in result string
            result = callAPI.execute(MainActivity.REQUEST_URL + "/market/scrape", data.toString()).get();

            // create json Object from result string if result string does not equal null
            assert result != null;
            JSONObject jsonResult = new JSONObject(result);

            // get jsonStoreArray from result json object
            JSONArray jsonStoreArray = jsonResult.getJSONArray("supermarket");

            // loop through jsonStoreArray and create store objects
            for (int i=0; i<jsonStoreArray.length(); i++) {
                // add store to stores array list
                stores.add(MainActivity.generateStoreFromJsonObject(jsonStoreArray.getJSONObject(i)));
            }

            Log.d(TAG, "requestStores: stores: " + stores);
        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // inform adapter that data has changed
        adapter.notifyDataSetChanged();

        // display stores if map is ready
        if (mapReady) displayStores();

        // log all stores
        Log.i(TAG, "requestStores: stores: " + stores);
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
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));

        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        mapReady = true;

        displayStores();
    }

    /**
     * Add markers for every store on map
     *
     * @since 1.0.0
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayStores() {

        // set up color marker
        float[] hsv = new float[3];
        Color.colorToHSV(Objects.requireNonNull(getContext()).getColor(R.color.darkBlue), hsv);

        // loop through all stores
        for (Store store : stores) {

            // get latitude and longitude from store
            double latitude = store.getLatitude();
            double longitude = store.getLongitude();

            // add new marker which represents the current store on map
            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(store.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));

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
        }

        updateCamera();

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
            CameraPosition position = new CameraPosition(lastKnownLocationLatLgn, 12.5f, 0f, 0f);

            // if map is ready move to camera to new position
            if (mapReady) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLocationChanged(Location location) {
        // update store lists
        lastKnownLocation = location;
        requestStores(location);
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
