package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import de.nivram710.crowd_stock_supermarket.MainActivity;
import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.store.Store;

public class RCCAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Store> stores;

    RCCAdapter(Context context, ArrayList<Store> stores) {
        this.context = context;
        this.stores = stores;
    }

    @Override
    public int getCount() {
        return stores.size();
    }

    @Override
    public Object getItem(int position) {
        return stores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.store_card, null);

        final Store store = stores.get(position);

        TextView textViewName = view.findViewById(R.id.text_view_store_name);
        textViewName.setText(store.getName());

        TextView textViewAddress = view.findViewById(R.id.text_view_store_address);
        String addressDistance = store.getAddress() + ", " + store.getCity() + " - " + MainActivity.getFormattedDistance(store.getDistance());
        textViewAddress.setText(addressDistance);

        TextView textViewIsOpen = view.findViewById(R.id.text_view_is_open);
        if(store.isOpen()) {
            textViewIsOpen.setText(context.getString(R.string.store_is_open));
            textViewIsOpen.setTextColor(context.getColor(R.color.holoGreenLight));
        } else {
            textViewIsOpen.setText(context.getString(R.string.store_is_closed));
            textViewIsOpen.setTextColor(context.getColor(R.color.holoRedDark));
        }

        // get product name text views
        TextView textViewProduct1 = view.findViewById(R.id.text_view_product_1);
        TextView textViewProduct2 = view.findViewById(R.id.text_view_product_2);
        TextView textViewProduct3 = view.findViewById(R.id.text_view_product_3);

        // set text for product name text views
        textViewProduct1.setText(store.getProducts().get(0).getName());
        textViewProduct2.setText(store.getProducts().get(1).getName());
        textViewProduct3.setText(store.getProducts().get(2).getName());

        // get color indicator text views
        TextView colorIndicatorProduct1 = view.findViewById(R.id.color_indicator_product_1);
        TextView colorIndicatorProduct2 = view.findViewById(R.id.color_indicator_product_2);
        TextView colorIndicatorProduct3 = view.findViewById(R.id.color_indicator_product_3);

        // set color for color indicator
        colorIndicatorProduct1.setBackgroundTintList(ColorStateList.valueOf(getIndicatorColor(store.getProducts().get(0).getAvailability())));
        colorIndicatorProduct2.setBackgroundTintList(ColorStateList.valueOf(getIndicatorColor(store.getProducts().get(1).getAvailability())));
        colorIndicatorProduct3.setBackgroundTintList(ColorStateList.valueOf(getIndicatorColor(store.getProducts().get(2).getAvailability())));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Store store = stores.get(position);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("store", store);

                context.startActivity(intent);
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getIndicatorColor(int availability) {
        if (availability > 0 && availability < 34) {
            return context.getColor(R.color.holoRedDark);
        } else if (availability > 33 && availability < 67) {
            return context.getColor(R.color.holoOrangeDark);
        } else if (availability > 66 && availability < 101) {
            return context.getColor(R.color.holoGreenLight);
        } else {
            return context.getColor(R.color.darkerGray);
        }
    }
}
