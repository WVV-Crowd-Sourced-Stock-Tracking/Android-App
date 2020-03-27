package de.nivram710.crowd_stock_supermarket;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import de.nivram710.crowd_stock_supermarket.connectivity.CallAPI;
import de.nivram710.crowd_stock_supermarket.store.Product;

public class MainActivity extends AppCompatActivity {

    public static final String REQUEST_URL = "https://wvvcrowdmarket.herokuapp.com/ws/rest";
    public static ArrayList<Product> allAvailableProducts = new ArrayList<>();
    public static int highestID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        CallAPI callAPI = new CallAPI();
        try {
            JSONObject resultObject = new JSONObject(callAPI.execute(REQUEST_URL + "/product/scrape", "{}").get());
            JSONArray allProductsJsonArray = resultObject.getJSONArray("product");

            for (int i = 0; i < allProductsJsonArray.length(); i++) {
                JSONObject productJsonObject = allProductsJsonArray.getJSONObject(i);
                allAvailableProducts.add(new Product(productJsonObject.getInt("product_id"),
                        productJsonObject.getString("product_name"), 101));
            }

            highestID = 0;
            for (Product product : allAvailableProducts) {
                highestID = Math.max(highestID, product.getId());
            }

        } catch (JSONException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getFormattedDistance(double distance) {
        String distanceString;
        if((int) distance > 1099) {
            double distanceInKm = distance / 1000;
            double distanceInKmRight = distanceInKm * 100;
            distanceInKm = (int) distanceInKmRight / 100d;
            distanceString = distanceInKm + "km";
        } else {
            distanceString = (int) distance + "m";
        }
        return distanceString;
    }

}
