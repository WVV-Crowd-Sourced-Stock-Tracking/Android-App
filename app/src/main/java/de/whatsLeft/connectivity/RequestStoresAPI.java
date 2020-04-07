package de.whatsLeft.connectivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;
import de.whatsLeft.store.Store;
import de.whatsLeft.ui.stores.LVSAdapter;

/**
 * This class is responsible for requesting all stores from backend.
 * It uses the postExecute method to display the markers and add the
 * stores, to avoid a freezing ui
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class RequestStoresAPI extends AsyncTask {

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 30000;

    private HttpURLConnection connection;

    @SuppressLint("StaticFieldLeak")
    private Context context;

    private GoogleMap googleMap;
    private String requestUrlString;
    private Location location;
    private ArrayList<Store> stores;
    private LVSAdapter adapter;

    private static final String TAG = "RequestStoresFromAPI";


    /**
     * Constructor
     *
     * @param context Context to access app colors
     * @param googleMap GoogleMap object to display marker on it
     * @param requestUrl String to the base request url /market/scrape is added in constructor
     * @param location Location object to parse the location in the json input for api
     * @param stores reference to stores arrayList from MarketsFragment to add new stores
     *
     * @since 1.0.0
     */
    public RequestStoresAPI(Context context, GoogleMap googleMap, String requestUrl, Location location, ArrayList<Store> stores, LVSAdapter adapter) {
        this.context = context;
        this.googleMap = googleMap;
        this.requestUrlString = requestUrl + "/market/scrape";
        this.location = location;
        this.stores = stores;
        this.adapter = adapter;
    }

    @Override
    protected String doInBackground(Object... objects) {
        Log.d(TAG, "doInBackground: called");

        // default successful is failed
        String result = "failed";

        // log request url and input data
        Log.i(TAG, "doInBackground: requestUrlString: " + requestUrlString);

        try {
            // create connection object
            URL requestUrl = new URL(requestUrlString);
            connection = (HttpURLConnection) requestUrl.openConnection();

            // setup connection
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            // prepare connection for post request
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // create input json object
            JSONObject inputJsonObject = new JSONObject();
            try {
                // store input data in json object
                inputJsonObject.put("latitude", location.getLatitude());
                inputJsonObject.put("longitude", location.getLongitude());
                inputJsonObject.put("radius", 2000);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.i(TAG, "doInBackground: inputData: " + inputJsonObject.toString());

            // add input data to connection
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(inputJsonObject.toString());
            streamWriter.flush();

            // connect to url
            connection.connect();

            // save response from backend
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // read response from backend
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            Log.d(TAG, "doInBackground: stringBuilder: " + stringBuilder.toString());

            // close reader
            reader.close();

            // store everything in result string
            result = stringBuilder.toString();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // disconnect from backend
            connection.disconnect();
        }

        // log if communication to backend was successful and the backend's response
        Log.i(TAG, "doInBackground: result: " + result);
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onPostExecute(Object o) {
        // get result String object
        String result = (String) o;

        // add new found stores to stores list
        addNewStoresToList(result);

        // display markers on map
        displayStores();

        // inform the adapter that the stores list might have changed
        adapter.notifyDataSetChanged();
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
                    Store newStore = MainActivity.generateStoreFromJsonObject(jsonStoreArray.getJSONObject(i));

                    // try to find it in list and if make isStoreInList true
                    boolean isStoreInList = false;
                    for(Store store : stores) {
                        assert newStore != null;
                        if (store.equals(newStore)) {
                            isStoreInList = true;
                            break;
                        }
                    }

                    // if store is not in list add store to stores array list
                    if(!isStoreInList) stores.add(newStore);
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
    private void displayStores() {

        // set up color marker
        float[] hsv = new float[3];
        Color.colorToHSV(context.getColor(R.color.darkBlue), hsv);

        // loop through all stores
        for (Store store : stores) {

            // get latitude and longitude from store
            double latitude = store.getLatitude();
            double longitude = store.getLongitude();

            // add new marker which represents the current store on map
            googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                    .title(store.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(hsv[0])));
        }
    }
}
