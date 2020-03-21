package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.nivram710.crowd_stock_supermarket.MainActivity;
import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.connectivity.HTTPGetRequest;
import de.nivram710.crowd_stock_supermarket.store.Product;
import de.nivram710.crowd_stock_supermarket.store.Store;

public class MarketsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "MarketsFragment";

    private GoogleMap mGoogleMap;
    private MapView mapView;
    private View mView;

    private boolean mapReady = false;

    private LocationManager locationManager;

    private MarketsViewModel marketsViewModel;
    private ListView listView;
    private ArrayList<Store> stores = new ArrayList<>();
    private RCCAdapter adapter;

    private Location lastKnownLocation;

    private String REQUEST_URL = "http://3.120.206.89";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_markets, container, false);
        listView = mView.findViewById(R.id.store_list_view);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, (LocationListener) this);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);


        adapter = new RCCAdapter(getContext(), stores);

        listView.setAdapter(adapter);
        return mView;
    }

    private void requestStores(Location location) {

        HTTPGetRequest getRequest = new HTTPGetRequest();
        String result = null;
        try {
            result = (String) getRequest.execute(REQUEST_URL + "/markets?latitude=" +  location.getLatitude() +"&longitude=" + location.getLongitude()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        try {
            jsonArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "requestStores: jsonArray: " + jsonArray);
        for (int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject object = (JSONObject) jsonArray.get(i);
                String id = object.getString("id");
                String name = object.getString("name");
                String address = object.getString("vicinity");
                double distance = object.getDouble("distance");
                double latitude = object.getDouble("latitude");
                double longitude = object.getDouble("longitude");
                boolean isOpen = object.getBoolean("open_now");

                Store store = new Store(id, name, address, distance, latitude, longitude, new Product[]{}, isOpen);
                Log.d(TAG, "requestStores: store created: " + store.toString());
                stores.add(store);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        if(mapReady) displayStores();
        Log.i(TAG, "requestStores: stores: " + stores);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = mView.findViewById(R.id.map);
        if(mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));

        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        LatLng lastKnownLocationLatLgn = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        CameraPosition position = new CameraPosition(lastKnownLocationLatLgn, 13f, 0f, 0f);
        mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        displayStores();
    }

    private void displayStores() {

        for (Store store : stores) {
            double latitude = store.getLatitude();
            double longitude = store.getLongitude();

            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(store.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .alpha(.99f));

        }

    }

    @Override
    public void onLocationChanged(Location location) {
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
