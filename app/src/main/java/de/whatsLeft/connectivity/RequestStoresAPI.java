package de.whatsLeft.connectivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.whatsLeft.FormatUtils;
import de.whatsLeft.MainActivity;
import de.whatsLeft.R;
import de.whatsLeft.store.Product;
import de.whatsLeft.store.Store;
import de.whatsLeft.ui.stores.LVSAdapter;

/**
 * Child class from APIRequest; processes given data to request stores and display them properly
 * @see APIRequest
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.2
 */
public class RequestStoresAPI extends APIRequest {

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private GoogleMap googleMap;
    private ArrayList<Store> stores;
    private LVSAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    private ListView storeListView;
    @SuppressLint("StaticFieldLeak")
    private LinearLayout progressUpdate;

    private boolean zipMode;

    /**
     * Constructor
     *
     * @param context Context to access project colors
     * @param googleMap GoogleMap object to display maker
     * @param location to get current location
     * @param stores ArrayList to loop through stores to check if the store is already in list
     * @param adapter LVSAdapter to inform hin about changes
     *
     * @see LVSAdapter
     *
     * @since 1.0.0
     */
    public RequestStoresAPI(Context context, GoogleMap googleMap, Location location, ArrayList<Store> stores, LVSAdapter adapter, ListView storeListView, LinearLayout progressUpdate, int searchRadius) {
        super("/market/scrape", createInputJSONObject(location, searchRadius).toString());
        this.context = context;
        this.googleMap = googleMap;
        this.stores = stores;
        this.adapter = adapter;
        this.storeListView = storeListView;
        this.progressUpdate = progressUpdate;
        zipMode = false;
    }

    /**
     * Constructor
     *
     * @param context Context to access project colors
     * @param googleMap GoogleMap object to display maker
     * @param zipCode String entered by user
     * @param stores ArrayList to loop through stores to check if the store is already in list
     * @param adapter LVSAdapter to inform hin about changes
     *
     * @see LVSAdapter
     *
     * @since 1.0.0
     */
    public RequestStoresAPI(Context context, GoogleMap googleMap, String zipCode, ArrayList<Store> stores, LVSAdapter adapter, ListView storeListView, LinearLayout progressUpdate, int searchRadius) {
        super("/market/scrape", createInputJSONObject(zipCode, searchRadius).toString());
        this.context = context;
        this.googleMap = googleMap;
        this.stores = stores;
        this.adapter = adapter;
        this.storeListView = storeListView;
        this.progressUpdate = progressUpdate;
        zipMode = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(String result) {
        // ad new found stores to stores list
        addNewStoresToList(result);

        // display markers on map
        ArrayList<Marker> markers = displayStores();

        // hide progress bar and show store list
        progressUpdate.setVisibility(View.GONE);
        storeListView.setVisibility(View.VISIBLE);

        // inform the adapter that the stores list might have changed
        adapter.notifyDataSetChanged();

        // if zip mode is enabled  move camera to show all markers
        if (zipMode) updateCamera(markers);
    }

    /**
     * Uses the position of every marker to create bounds for the camera
     *
     * @param markers ArrayList containing all markers
     * @since 1.0.0
     */
    private void updateCamera(ArrayList<Marker> markers) {
        // create an LatLngBuilder object
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        // include each marker position to bounds
        for (Marker marker : markers) {
            LatLng latLng = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
            builder.include(latLng);
        }

        // create bounds object
        LatLngBounds bounds = builder.build();

        // move camera to fit bounds
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
    }

    /**
     * This method adds the new stores from backend to stores list
     *
     * @param result a string containing the result json object from backend
     * @since 1.0.0
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addNewStoresToList(String result) {
        if (!result.equals("failed")) {
            try {

                // create json Object from result string if result string does not equal null
                JSONObject jsonResult = new JSONObject(result);

                // get jsonStoreArray from result json object
                JSONArray jsonStoreArray = jsonResult.getJSONArray("supermarket");

                // loop through jsonStoreArray and create store objects
                for (int i = 0; i < jsonStoreArray.length(); i++) {

                    // create newStore object
                    Store newStore = FormatUtils.generateStoreFromJsonObject(jsonStoreArray.getJSONObject(i));

                    // if there is no store in stores add it
                    if(stores.size() == 0) stores.add(newStore);

                    // try to find it in list and if make isStoreInList true
                    boolean isStoreInList = false;
                    if (stores.size() > 0) {
                        for (Store store : stores) {
                            assert newStore != null;
                            if (store.equals(newStore)) {
                                isStoreInList = true;
                                break;
                            }
                        }

                        // if store is not in list add store to stores array list
                        if (!isStoreInList) stores.add(newStore);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Creates markers on the map representing the locations of the stores
     *
     * @since 1.0.0
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private ArrayList<Marker> displayStores() {

        // set up color marker
        float[] hsv = new float[3];
        Color.colorToHSV(context.getColor(R.color.darkBlue), hsv);

        ArrayList<Marker> markers = new ArrayList<>();

        // loop through all stores
        for (Store store : stores) {

            // get latitude and longitude from store
            double latitude = store.getLatitude();
            double longitude = store.getLongitude();

            // add new marker which represents the current store on map
            Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(store.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));
            markers.add(marker);
        }
        return markers;
    }

    /**
     * Creates the input json object for constructor based on current location
     *
     * @param location current location
     * @param searchRadius int max distance in kilometers from you current location
     * @return inputJsonObject containing coordinates and search radius
     * @since 1.0.0
     */
    private static JSONObject createInputJSONObject(Location location, int searchRadius) {

        try {
            JSONObject inputJsonObject = new JSONObject();

            // store input data in json object
            inputJsonObject.put("latitude", location.getLatitude());
            inputJsonObject.put("longitude", location.getLongitude());
            inputJsonObject.put("radius", searchRadius);

            // if there are some selected products in filter view add their product ids to a json array
            if (MainActivity.selectedProducts.size() > 0) {

                // create empty jsonArray which contains the product ids
                JSONArray productIds = new JSONArray();

                // for each selected product add it's product id to the json array
                for (Product product : MainActivity.selectedProducts) {
                    productIds.put(product.getId());
                }

                // add selected products to input json object
                inputJsonObject.put("product_id", productIds);

            }

            return inputJsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    /**
     * Creates the input json object for constructor based on current location
     *
     * @param zipCode String of zip code entered by user
     * @param searchRadius int max distance in kilometers from zip codes center location
     * @return inputJsonObject containing coordinates and search radius
     * @since 1.0.0
     */
    private static JSONObject createInputJSONObject(String zipCode, int searchRadius) {

        try {
            JSONObject inputJsonObject = new JSONObject();

            // store input data in json object
            inputJsonObject.put("zip", zipCode);
            inputJsonObject.put("radius", searchRadius);

            // if there are some selected products in filter view add their product ids to a json array
            if (MainActivity.selectedProducts.size() > 0) {

                // create empty jsonArray which contains the product ids
                JSONArray productIds = new JSONArray();

                // for each selected product add it's product id to the json array
                for (Product product : MainActivity.selectedProducts) {
                    productIds.put(product.getId());
                }

                // add selected products to input json object
                inputJsonObject.put("product_id", productIds);

            }

            return inputJsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }
}
