package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import de.nivram710.crowd_stock_supermarket.R;

public class DetailActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // store all extras in variables
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        String storeId = extras.getString("id");
        String name = extras.getString("name");
        String address = extras.getString("address");
        boolean isOpen = extras.getBoolean("isOpen");

        // display store name
        TextView textViewStoreName = findViewById(R.id.text_view_store_name);
        textViewStoreName.setText(name);

        // display store address
        TextView textViewStoreAddress = findViewById(R.id.text_view_address);
        textViewStoreAddress.setText(address);

        // display is open status
        TextView textViewIsOpen = findViewById(R.id.text_view_is_open);
        if(isOpen) {
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
                    editButton.setImageDrawable(getDrawable(R.drawable.ic_mode_edit_white_24dp));
                }
            }
        });

    }
}
