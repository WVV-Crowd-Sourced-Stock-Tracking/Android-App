package de.nivram710.crowd_stock_supermarket.ui.markets;

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
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import de.nivram710.crowd_stock_supermarket.MainActivity;
import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.connectivity.CallAPI;
import de.nivram710.crowd_stock_supermarket.store.Product;
import de.nivram710.crowd_stock_supermarket.store.Store;

public class MarketsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private static final String TAG = "MarketsFragment";

    private GoogleMap mGoogleMap;
    private View mView;

    private boolean mapReady = false;

    private Location lastKnownLocation;

    private ArrayList<Store> stores = new ArrayList<>();
    private RCCAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_markets, container, false);
        ListView listView = mView.findViewById(R.id.store_list_view);

        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);


        adapter = new RCCAdapter(getContext(), stores);

        listView.setAdapter(adapter);
        return mView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestStores(Location location) {

        CallAPI callAPI = new CallAPI();
        String result = null;

        JSONObject data = new JSONObject();
        try {
            data.put("gps_width", String.valueOf(location.getLatitude()));
            data.put("gps_length", String.valueOf(location.getLongitude()));
            data.put("radius", 2000);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            result = callAPI.execute(MainActivity.REQUEST_URL + "/market/scrape", data.toString()).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject jsonResult;
        JSONArray jsonStoreArray = null;
        try {
            assert result != null;
            jsonResult = new JSONObject(result);
            jsonStoreArray = jsonResult.getJSONArray("supermarket");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        assert jsonStoreArray != null;
        for (int i = 0; i < jsonStoreArray.length(); i++) {
            try {
                JSONObject object = (JSONObject) jsonStoreArray.get(i);
                String id = object.getString("id");
                String name = object.getString("name");
                String address = object.getString("street");
                double distance = object.getDouble("distance");
                double latitude = object.getDouble("lat");
                double longitude = object.getDouble("lng");
                boolean isOpen = object.getBoolean("open");

                ArrayList<Product> products = generateProductsList(object);

                Store store = new Store(id, name, address, distance, latitude, longitude, products, isOpen);
                Log.d(TAG, "requestStores: store created: " + store.toString());
                stores.add(store);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
        if (mapReady) displayStores();
        Log.i(TAG, "requestStores: stores: " + stores);
    }

    private ArrayList<Product> generateProductsList(JSONObject jsonStoreObject) {

        ArrayList<Product> products = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonStoreObject.getJSONArray("products");
            if (jsonArray.length() == 0) {

                CallAPI callAPI = new CallAPI();
                String resultString = callAPI.execute(MainActivity.REQUEST_URL + "/product/scrape", "{}").get();

                JSONObject resultJsonObject = new JSONObject(resultString);
                jsonArray = resultJsonObject.getJSONArray("product");
            }

                for (int i=0; i<jsonArray.length(); i++) {

                    // get current product from array as jsonO object
                    JSONObject jsonProduct = jsonArray.getJSONObject(i);

                    // get product id and name
                    int id = jsonProduct.getInt("product_id");
                    String name = jsonProduct.getString("product_name");

                    // add new product to products array list
                    products.add(new Product(id, name, "", 0));
            }

        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayStores() {

        for (Store store : stores) {
            double latitude = store.getLatitude();
            double longitude = store.getLongitude();

            float[] hsv = new float[3];
            Color.colorToHSV(Objects.requireNonNull(getContext()).getColor(R.color.darkBlue), hsv);

            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(store.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));
        }

        // update camera
        if (lastKnownLocation != null) {
            LatLng lastKnownLocationLatLgn = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            CameraPosition position = new CameraPosition(lastKnownLocationLatLgn, 13f, 0f, 0f);
            if (mapReady) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
