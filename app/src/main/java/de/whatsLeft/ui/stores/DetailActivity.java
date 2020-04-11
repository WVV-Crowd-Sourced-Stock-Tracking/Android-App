package de.whatsLeft.ui.stores;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;
import de.whatsLeft.connectivity.TransmitProductStock;
import de.whatsLeft.store.ProductComparator;
import de.whatsLeft.store.Store;

/**
 * Activity to show the details including all products and their availability of a store
 * <p>This Activity has a view mode and a edit mode. In view mode the products and their availabilities
 * are just display while in edit mode the user is able to update the products stock</p>
 *
 * <p>Products in listView are managed by LVPAdapter or LVPEAdapter whether the edit mode is enabled
 * or nor</p>
 *
 * @see LVPAdapter
 * @see LVPEAdapter
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.1
 */
public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mGoogleMap;
    private boolean mapReady = false;
    private boolean editModeEnabled = false;
    private FloatingActionButton editButton;
    private Location lastKnownLocation;
    private Store store;

    private ListView listView;
    private TextView textViewStock;
    private TextView textViewEmpty;
    private TextView textViewLess;
    private TextView textViewEnough;

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

        textViewStock = findViewById(R.id.text_view_stock);
        textViewEmpty = findViewById(R.id.text_view_empty);
        textViewLess = findViewById(R.id.text_view_less);
        textViewEnough = findViewById(R.id.text_view_enough);

        assert store != null;

        // display store name
        TextView textViewStoreName = findViewById(R.id.text_view_store_name);
        textViewStoreName.setText(store.getName());

        // display store address
        TextView textViewStoreAddress = findViewById(R.id.text_view_address);

        String addressString = store.getAddress() + ", " + store.getCity() + " - " + MainActivity.getFormattedDistance(store.getDistance());
        textViewStoreAddress.setText(addressString);

        // create new IndicatorUtils object
        IndicatorUtils indicatorUtils = new IndicatorUtils(this);

        // display is open status
        TextView textViewIsOpen = findViewById(R.id.text_view_is_open);
        textViewIsOpen.setTextColor(indicatorUtils.getStoreOpenTextColor(store));
        textViewIsOpen.setText(indicatorUtils.getStoreOpenText(store));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            store.getProducts().sort(new ProductComparator());
        }

        // setup adapter for listView
        listView = findViewById(R.id.list_view_products);
        LVPAdapter adapter = new LVPAdapter(this, store.getProducts());
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

    /**
     * This method sets everything up for the edit mode and enables it
     *
     * @since 1.0.0
     */
    private void enableEditMode() {

        // change icon from floating action button to save icon
        editButton.setImageDrawable(getDrawable(R.drawable.ic_save_white_24dp));

        // hide text view to indicate that in this column the stock data is displayed
        textViewStock.setVisibility(View.GONE);

        // show text views which explain what each radio button and color stands for
        textViewEmpty.setVisibility(View.VISIBLE);
        textViewLess.setVisibility(View.VISIBLE);
        textViewEnough.setVisibility(View.VISIBLE);

        // create new LVPEAdapter and set this as the list view's adapter
        LVPEAdapter lvpeAdapter = new LVPEAdapter(this, store.getProducts());
        listView.setAdapter(lvpeAdapter);

        // log that edit mode was enabled successfully and set editModeEnabled to true
        Log.i(TAG, "enableEditMode: editMode initiated successfully");
        editModeEnabled = true;
    }

    /** This method sets everything up for the view mode and disables edit mode
     *
     * @since 1.0.0
     */
    private void disableEditMode() {

        // change icon of floating action button to edit icon
        editButton.setImageDrawable(getDrawable(R.drawable.ic_mode_edit_white_24dp));

        // hide explanation of radio buttons and color
        textViewEmpty.setVisibility(View.GONE);
        textViewLess.setVisibility(View.GONE);
        textViewEnough.setVisibility(View.GONE);

        // show that this column show data about stock
        textViewStock.setVisibility(View.VISIBLE);

        // transmit data to backend
        new TransmitProductStock(this, store).execute();

        // tell the user whether the transmit to backend was successful or not
//        if (transmitSuccessful) Toast.makeText(this, getString(R.string.transmit_successful), Toast.LENGTH_LONG).show();
//        else Toast.makeText(this, getString(R.string.transmit_failed), Toast.LENGTH_LONG).show();

        // create new object of LVPVAdapter and set it as listView's adapter
        LVPAdapter lvpAdapter = new LVPAdapter(this, store.getProducts());
        listView.setAdapter(lvpAdapter);

        // log that edit mode was disabled successful and set editModeEnabled to false
        Log.i(TAG, "disableEditMode: edit mode closed successfully");
        editModeEnabled = false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLocationChanged(Location location) {

        // store location as lastKnownLocation
        lastKnownLocation = location;

        // update camera
        if (mapReady) updateCamera();

        // log location
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

        // setup google map view
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(false);
        mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.style_json)));

        // setup color for store markers
        float[] hsv = new float[3];
        Color.colorToHSV(getColor(R.color.darkBlue), hsv);

        // log the coordinates of store
        Log.d(TAG, "onMapReady: latitude: " + store.getLatitude());
        Log.d(TAG, "onMapReady: longitude: " + store.getLongitude());

        // add marker for the store on map
        mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(store.getLatitude(), store.getLongitude()))
                .title(store.getName())
                .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));

        // log lastKnownLocation
        Log.d(TAG, "onMapReady: lastKnownLocation: " + lastKnownLocation);

        // set camera to display current location and store location
        updateCamera();

        // set mapReady to true;
        mapReady = true;
    }

    public void updateCamera() {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()));
        builder.include(new LatLng(store.getLatitude(), store.getLongitude()));

        LatLngBounds bounds = builder.build();
        int padding = 100;

        if (mapReady) mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

    }
}
