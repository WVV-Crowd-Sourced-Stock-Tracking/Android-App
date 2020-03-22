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
import android.widget.RadioGroup;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.nivram710.crowd_stock_supermarket.MainActivity;
import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.connectivity.CallAPI;
import de.nivram710.crowd_stock_supermarket.store.Product;
import de.nivram710.crowd_stock_supermarket.store.Store;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mGoogleMap;

    private boolean mapReady = false;

    Location lastKnownLocation;

    Store store;

    private static final String TAG = "DetailActivity";

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

        store.setId("30");
        Product product = new Product(47, "Pi", "", 100);
        store.addProduct(product);

        // display store address
        TextView textViewStoreAddress = findViewById(R.id.text_view_address);
        textViewStoreAddress.setText(store.getAddress());

        // display is open status
        TextView textViewIsOpen = findViewById(R.id.text_view_is_open);
        if(store.isOpen()) {
            textViewIsOpen.setText(getString(R.string.store_is_open));
            textViewIsOpen.setTextColor(getColor(R.color.holoGreenLight));
        } else {
            textViewIsOpen.setText(getString(R.string.store_is_closed));
            textViewIsOpen.setTextColor(getColor(R.color.holoGreenLight));
        }

        // Create RadioButtonGroups objects
        final RadioGroup radioGroupMilk = findViewById(R.id.radio_group_milk);
        RadioGroup radioGroupBread = findViewById(R.id.radio_group_bread);
        RadioGroup radioGroupToiletPaper = findViewById(R.id.radio_group_toilet_paper);

        final ArrayList<RadioGroup> radioGroups = new ArrayList<>();
        radioGroups.add(radioGroupMilk);
        radioGroups.add(radioGroupBread);
        radioGroups.add(radioGroupToiletPaper);

        // create onClickListener
        final FloatingActionButton editButton = findViewById(R.id.floating_action_button_edit_mode);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // toggle radio buttons clickable
                for(RadioGroup radioGroup : radioGroups) {
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setClickable(!radioGroup.getChildAt(i).isClickable());
                    }
                }

                // toggle icon of fab
                if(radioGroups.get(0).getChildAt(0).isClickable()) {
                    editButton.setImageDrawable(getDrawable(R.drawable.ic_save_white_24dp));
                } else {
                    transmitNewData();
                    editButton.setImageDrawable(getDrawable(R.drawable.ic_mode_edit_white_24dp));
                }
            }
        });

    }

    void transmitNewData() {

        Log.d(TAG, "transmitNewData: called");

        for (Product product : store.getProducts()) {
            CallAPI callAPI = new CallAPI();
            String resultString = "";
            try {

                JSONObject update = new JSONObject();
                update.put("market_id", store.getId());
                update.put("product_id", product.getId());
                update.put("quantity", product.getAvailability());

                resultString = callAPI.execute(MainActivity.REQUEST_URL + "/market/transmit", update.toString()).get();
                Log.d(TAG, "transmitNewData: callAPI executed");

            } catch (ExecutionException | InterruptedException | JSONException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "transmitNewData: resultString: " + resultString);
        }
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
