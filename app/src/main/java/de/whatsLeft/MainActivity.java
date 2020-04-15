package de.whatsLeft;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import de.whatsLeft.connectivity.RequestProductAPI;
import de.whatsLeft.store.Product;

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

}
