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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import de.whatsLeft.connectivity.CallAPI;
import de.whatsLeft.store.Product;
import de.whatsLeft.store.ProductComparator;
import de.whatsLeft.store.Store;

/**
 * MainActivity with this everything start
 * 
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity {

    // public static final String REQUEST_URL = "https://wvvcrowdmarket.herokuapp.com/ws/rest";
    public static final String REQUEST_URL = "https://wvv2.herokuapp.com/ws/rest";

    public static ArrayList<Product> availableProducts = new ArrayList<>();
    public static int highestID;

    private static final String TAG = "MainActivity";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // setup navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        // store all products in available products
        availableProducts = getAllProducts();

        // findHighestId of all available Products
        highestID = findHighestID(availableProducts);

    }

    /**
     * Fetches all products from backend and stores them into an array list
     *
     * @return products ArrayList<Product>; an array list containing all products available by backend
     * @since 1.0.0
     */
    private ArrayList<Product> getAllProducts() {
        // create new and empty products array list
        ArrayList<Product> products = new ArrayList<>();

        // create new callAPI object to connect to backend
        CallAPI callAPI = new CallAPI();
        try {
            // connect to backend and store response in a json object
            JSONObject resultObject = new JSONObject(callAPI.execute(REQUEST_URL + "/product/scrape", "{}").get());

            // get products array from backend response
            JSONArray allProductsJsonArray = resultObject.getJSONArray("product");

            // loop through all products
            for (int i = 0; i < allProductsJsonArray.length(); i++) {

                // get json product object from array
                JSONObject productJsonObject = allProductsJsonArray.getJSONObject(i);

                // generate and add product to products array list
                products.add(generateProductFromJsonObject(productJsonObject));
            }

        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return products;
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
            ArrayList<Product> products = generateProductsList(jsonProductArray);

            // sort products list
            products.sort(new ProductComparator());

            // look if store opens today
            int indexOfCurrentPeriod = TimeUtils.findPeriodForCurrentDay(jsonPeriodsArray);

            // if no openingDayId was found the store is closed for this day
            boolean openingToday = indexOfCurrentPeriod != -1;

            if(openingToday && jsonPeriodsArray.length() > 0) {

                // get period object for current day
                JSONObject jsonPeriodObject = jsonPeriodsArray.getJSONObject(indexOfCurrentPeriod);

                Date openingDate = TimeUtils.generateDateFromPeriods(jsonPeriodObject, false);
                Date closingDate = TimeUtils.generateDateFromPeriods(jsonPeriodObject, true);

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

    /**
     * Generates a products array list with product objects from a products json array
     *
     * @param jsonProductsArray containing all products
     * @return products array list containing all products for the store
     * @since 1.0.0
     */
    private static ArrayList<Product> generateProductsList(JSONArray jsonProductsArray) {
        // create empty products list
        ArrayList<Product> products = new ArrayList<>();

        // create array to indicate whether for the given product id a product is available in store
        boolean[] productsInList = new boolean[MainActivity.highestID + 1];
        Arrays.fill(productsInList, Boolean.FALSE);

        try {
            for (int i = 0; i < jsonProductsArray.length(); i++) {
                JSONObject productJsonObject = jsonProductsArray.getJSONObject(i);

                // get all relevant attributes from json object
                int id = productJsonObject.getInt("product_id");
                String name = productJsonObject.getString("product_name");
                String emoticon = productJsonObject.getString("emoji");
                int availability = productJsonObject.getInt("availability");


                // create product and store it in array list
                Product product = new Product(id, name, emoticon, availability);
                products.add(product);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // store true in array where index equals product id to indicate that product is already in store
        for (Product product : products) {
            productsInList[product.getId()] = true;
        }

        // add all missing products to store
        // loop through all available Products
        for (Product product : MainActivity.availableProducts) {

            // and check if the product is already in product list
            if (!productsInList[product.getId()]) {
                try {
                    // if not clone the current product and add it to products list
                    products.add((Product) product.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(TAG, "generateProductsList: products: " + products);

        return products;
    }

    /**
     * Generates a product object from the given json object
     *
     * @param productJsonObject JsonObject; a json object containing all product information
     * @return product Product; the product object which attributes are equal to the json object's ones
     * @since 1.0.0
     */
    private static Product generateProductFromJsonObject(JSONObject productJsonObject) {
        try {
            // get all attributes from json object
            int id = productJsonObject.getInt("product_id");
            String name = productJsonObject.getString("product_name");
            String emoticon = productJsonObject.getString("emoji");

            // set availability of product
            int availability;

            // check if product json object has a availability field
            if (productJsonObject.has("availability")) {
                // if it has one; the availability will be set to the json object's value
                availability = productJsonObject.getInt("availability");
            } else {
                // else it will be 101 to indicate that there are no further information about it's availability
                availability = 101;
            }

            // create new product object
            Product product = new Product(id, name, emoticon, availability);

            Log.d(TAG, "generateProductFromJsonObject: new Product: " + product);

            return product;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // if creation failed return null
        return null;
    }

    /**
     * This method finds the highest ID of the given product list
     *
     * @return highestID int; The highest ProductID of given products array list
     * @since 1.0.0
     */
    private int findHighestID(ArrayList<Product> products) {
        int highestID = 0;
        for (Product product : products) {
            highestID = Math.max(highestID, product.getId());
        }
        return highestID;
    }

    /**
     * If the distance is less then 1000 method returns distance in meters + unit; else it return
     * distance in kilometers with unit
     *
     * @param distance double; distance attribute returned by backend
     * @return formattedDistance String; distance with unit added
     * @since 1.0.0
     */
    public static String getFormattedDistance(double distance) {

        // create string object that will be returned later
        String distanceString;

        // check if rounded distance is greater then 1000
        if ((int) distance > 1000) {

            // if distance is greater then 1000m convert distance into kilometers
            double distanceTemp = distance / 1000;
            double distanceInKm = (double) Math.round(distanceTemp * 100) / 100;
            distanceString = distanceInKm + "km";

        } else {
            // if not display distance in meters
            distanceString = (int) distance + "m";
        }

        // return distance as a string
        return distanceString;
    }

}
