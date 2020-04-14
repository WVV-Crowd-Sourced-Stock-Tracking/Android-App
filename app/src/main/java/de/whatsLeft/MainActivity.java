package de.whatsLeft;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.whatsLeft.connectivity.RequestProductAPI;
import de.whatsLeft.store.Product;
import de.whatsLeft.store.ProductComparator;
import de.whatsLeft.store.Store;

/**
 * MainActivity with this everything start
 * 
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.2
 */
public class MainActivity extends AppCompatActivity {

    public static ArrayList<Product> availableProducts = new ArrayList<>();
    public static int highestID;

    public static ArrayList<Product> selectedProducts = new ArrayList<>();

    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // setup navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        // get all available products and the highest product id
        new RequestProductAPI().execute();

    }

    /**
     * Generates a product object from the given json object
     *
     * @param jsonStoreObject JsonObject; a json object containing all store information
     * @return store Store; the store object which attributes are equal to the json object's ones
     * @since 1.0.0
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Store generateStoreFromJsonObject(JSONObject jsonStoreObject) {

        // create empty store object
        Store store;

        try {
            // get store attributes from json object
            String id = jsonStoreObject.getString("market_id");
            String name = jsonStoreObject.getString("market_name");
            String address = jsonStoreObject.getString("street");
            String city = jsonStoreObject.getString("city");
            double distance = jsonStoreObject.getDouble("distance");
            double latitude = jsonStoreObject.getDouble("latitude");
            double longitude = jsonStoreObject.getDouble("longitude");
            JSONArray jsonProductArray = jsonStoreObject.getJSONArray("products");
            JSONArray jsonPeriodsArray = jsonStoreObject.getJSONArray("periods");

            // generate product from current json object in products array list
            ArrayList<Product> products = FormatUtils.generateProductsList(jsonProductArray);

            // sort products list
            products.sort(new ProductComparator());

            // look if store opens today
            int indexOfCurrentPeriod = FormatUtils.findPeriodForCurrentDay(jsonPeriodsArray);

            // if no openingDayId was found the store is closed for this day
            boolean openingToday = indexOfCurrentPeriod != -1;

            if(openingToday && jsonPeriodsArray.length() > 0) {

                // get period object for current day
                JSONObject jsonPeriodObject = jsonPeriodsArray.getJSONObject(indexOfCurrentPeriod);

                Date openingDate = FormatUtils.generateDateFromPeriods(jsonPeriodObject, false);
                Date closingDate = FormatUtils.generateDateFromPeriods(jsonPeriodObject, true);

                // create new Store object
                store = new Store(id, name, address, city, distance, latitude, longitude, products, true, openingDate, closingDate);

            } else {
                // create new Store object
                store = new Store(id, name, address, city, distance, latitude, longitude, products, openingToday);
            }


            Log.d(TAG, "generateStoreFromJsonObject: new Store: " + store);
            
            // create and return new store object and set open to false for now
            return store;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // return null if creation was not successful
        return null;
    }

}
