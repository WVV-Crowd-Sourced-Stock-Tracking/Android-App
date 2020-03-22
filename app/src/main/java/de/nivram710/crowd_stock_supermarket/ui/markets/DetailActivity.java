package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import de.nivram710.crowd_stock_supermarket.R;

public class DetailActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String storeId = extras.getString("id");
        String name = extras.getString("name");
        String address = extras.getString("address");
        boolean isOpen = extras.getBoolean("isOpen");

        TextView textViewStoreName = findViewById(R.id.text_view_store_name);
        textViewStoreName.setText(name);

        TextView textViewStoreAddress = findViewById(R.id.text_view_address);
        textViewStoreAddress.setText(address);

        TextView textViewIsOpen = findViewById(R.id.text_view_is_open);
        if(isOpen) {
            textViewIsOpen.setText(getString(R.string.store_is_open));
            textViewIsOpen.setTextColor(getColor(R.color.holoGreenLight));
        } else {
            textViewIsOpen.setText(getString(R.string.store_is_closed));
            textViewIsOpen.setTextColor(getColor(R.color.holoGreenLight));
        }

    }
}
