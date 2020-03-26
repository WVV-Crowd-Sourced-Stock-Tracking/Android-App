package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import de.nivram710.crowd_stock_supermarket.MainActivity;
import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.connectivity.CallAPI;
import de.nivram710.crowd_stock_supermarket.store.Product;
import de.nivram710.crowd_stock_supermarket.store.ProductComparator;
import de.nivram710.crowd_stock_supermarket.store.Store;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mGoogleMap;
    private boolean mapReady = false;
    private boolean editModeEnabled = false;
    private FloatingActionButton editButton;
    private Location lastKnownLocation;
    private Store store;

    private ListView listView;

    private static final String TAG = "DetailActivity";

    public DetailActivity() {
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MapView mapView = findViewById(R.id.mapView);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        // store all extras in variables
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        store = (Store) extras.getSerializable("store");
        Log.d(TAG, "onCreate: store: " + store);

        assert store != null;

        // display store name
        TextView textViewStoreName = findViewById(R.id.text_view_store_name);
        textViewStoreName.setText(store.getName());

        // display store address
        TextView textViewStoreAddress = findViewById(R.id.text_view_address);

        String addressString = store.getAddress() + ", " + store.getCity() + " - " + MainActivity.getFormattedDistance(store.getDistance());
        textViewStoreAddress.setText(addressString);

        // display is open status
        TextView textViewIsOpen = findViewById(R.id.text_view_is_open);
        if (store.isOpen()) {
            textViewIsOpen.setText(getString(R.string.store_is_open));
            textViewIsOpen.setTextColor(getColor(R.color.holoGreenLight));
        } else {
            textViewIsOpen.setText(getString(R.string.store_is_closed));
            textViewIsOpen.setTextColor(getColor(R.color.holoRedDark));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            store.getProducts().sort(new ProductComparator());
        }

        // setup adapter for listView
        listView = findViewById(R.id.list_view_products);
        RVPAdapter adapter = new RVPAdapter(this, store.getProducts());
        listView.setAdapter(adapter);


        // create onClickListener
        editButton = findViewById(R.id.floating_action_button_edit_mode);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editModeEnabled) disableEditMode();
                else enableEditMode();

            }
        });

    }

    private void enableEditMode() {

        editButton.setImageDrawable(getDrawable(R.drawable.ic_save_white_24dp));

        RVPEAdapter rvpeAdapter = new RVPEAdapter(this, store.getProducts());
        listView.setAdapter(rvpeAdapter);

        Log.i(TAG, "enableEditMode: editMode initiated successfully");
        editModeEnabled = true;
    }

    @SuppressLint("ShowToast")
    private void disableEditMode() {

        editButton.setImageDrawable(getDrawable(R.drawable.ic_mode_edit_white_24dp));

        boolean transmitSuccessful = transmitData();
        if (transmitSuccessful)
            Toast.makeText(this, getString(R.string.transmit_successful), Toast.LENGTH_LONG);
        else Toast.makeText(this, getString(R.string.transmit_failed), Toast.LENGTH_LONG);

        RVPAdapter rvpAdapter = new RVPAdapter(this, store.getProducts());
        listView.setAdapter(rvpAdapter);


        Log.i(TAG, "disableEditMode: edit mode closed successfully");
        editModeEnabled = false;
    }

    private boolean transmitData() {

        boolean transmitSuccessful = true;

        for (Product product : store.getProducts()) {
            if (product.getAvailability() < 100) {
                CallAPI callAPI = new CallAPI();
                try {

                    JSONObject data = new JSONObject();
                    data.put("market_id", store.getId());
                    data.put("product_id", product.getId());
                    data.put("quantity", product.getAvailability());

                    JSONObject resultJsonObject = new JSONObject(callAPI.execute(MainActivity.REQUEST_URL + "/market/transmit", data.toString()).get());
                    if (!resultJsonObject.getString("result").equals("success"))
                        transmitSuccessful = false;
                    Log.i(TAG, "transmitData: product: " + product + "; transmit: " + resultJsonObject.getString("result"));
                } catch (JSONException | ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return transmitSuccessful;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLocationChanged(Location location) {

        lastKnownLocation = location;

        // update camera
        if (location != null) {
            LatLng lastKnownLocationLatLgn = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            CameraPosition position = new CameraPosition(lastKnownLocationLatLgn, 13f, 0f, 0f);
            if (mapReady) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));

        float[] hsv = new float[3];
        Color.colorToHSV(getColor(R.color.darkBlue), hsv);

        Log.d(TAG, "onMapReady: latitude: " + store.getLatitude());
        Log.d(TAG, "onMapReady: longitude: " + store.getLongitude());

        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(store.getLatitude(), store.getLongitude()))
                .title(store.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

        Log.d(TAG, "onMapReady: lastKnownLocation: " + lastKnownLocation);

        // update camera
        if (lastKnownLocation != null) {
            LatLng lastKnownLocationLatLgn = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            CameraPosition position = new CameraPosition(lastKnownLocationLatLgn, 13f, 0f, 0f);
            if (mapReady) mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
        }

        mapReady = true;
    }
}
