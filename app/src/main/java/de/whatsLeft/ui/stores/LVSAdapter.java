package de.whatsLeft.ui.stores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import de.whatsLeft.FormatUtils;
import de.whatsLeft.R;
import de.whatsLeft.store.Product;
import de.whatsLeft.store.Store;
import de.whatsLeft.ui.IndicatorUtils;
import de.whatsLeft.ui.detailActivity.DetailActivity;

/**
 * LVSAdapter --> ListViewStoresAdapter
 * Adapter to manage stores displayed in MarketsFragment
 *
 * @see MarketsFragment
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.1
 */
public class LVSAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Store> stores;

    private static final String TAG = "LVSAdapter";

    /**
     * Constructor
     *
     * @param context context to access views
     * @param stores array list containing all stores to add them to list view
     * @since 1.0.0
     */
    LVSAdapter(Context context, ArrayList<Store> stores) {
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

    /**
     * Returns position of store in store list
     *
     * @param latitude latitude of searched store object
     * @param longitude longitude of searched store object
     * @return position position of store in the list view
     * @since 1.0.0
     */
    int getPosition(double latitude, double longitude) {
        // loop through all stores in list until searched store is found
        for (int i = 0; i < stores.size(); i++) {

            // get current store object
            Store store = stores.get(i);

            // check if the store's latitude and longitude are equal to the parameters
            if (store.getLatitude() == latitude && store.getLongitude() == longitude) {
                Log.d(TAG, "getPosition: position: " + i);
                return i;
            }
        }

        // else return 0 to jump to top of the list
        return 0;
    }

    @SuppressLint("ViewHolder")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.store_card, null);

        // create new IndicatorUtils object
        IndicatorUtils indicatorUtils = new IndicatorUtils(context);

        // get store
        final Store store = stores.get(position);

        // display name of store as card title
        TextView textViewName = view.findViewById(R.id.text_view_store_name);
        textViewName.setText(store.getName());

        // display the stores address
        TextView textViewAddress = view.findViewById(R.id.text_view_store_address);
        String addressDistance = store.getAddress() + ", " + store.getCity() + " - " + FormatUtils.getFormattedDistance(store.getDistance());
        textViewAddress.setText(addressDistance);

        // display if the store is open or not
        TextView textViewIsOpen = view.findViewById(R.id.text_view_is_open);
        textViewIsOpen.setTextColor(indicatorUtils.getStoreOpenTextColor(store));
        textViewIsOpen.setText(indicatorUtils.getStoreOpenText(store));

        // get first three products
        Product product1 = store.getProducts().get(0);
        Product product2 = store.getProducts().get(1);
        Product product3 = store.getProducts().get(2);

        // get the first three product name text views
        TextView textViewProduct1 = view.findViewById(R.id.text_view_product_1);
        TextView textViewProduct2 = view.findViewById(R.id.text_view_product_2);
        TextView textViewProduct3 = view.findViewById(R.id.text_view_product_3);

        // display the name of first three products
        textViewProduct1.setText(product1.getName());
        textViewProduct2.setText(product2.getName());
        textViewProduct3.setText(product3.getName());

        // get color indicator text views
        TextView colorIndicatorProduct1 = view.findViewById(R.id.color_indicator_product_1);
        TextView colorIndicatorProduct2 = view.findViewById(R.id.color_indicator_product_2);
        TextView colorIndicatorProduct3 = view.findViewById(R.id.color_indicator_product_3);

        // set color of color indicator for first three products
        colorIndicatorProduct1.setBackgroundTintList(ColorStateList.valueOf(indicatorUtils.getIndicatorColor(product1)));
        colorIndicatorProduct2.setBackgroundTintList(ColorStateList.valueOf(indicatorUtils.getIndicatorColor(product2)));
        colorIndicatorProduct3.setBackgroundTintList(ColorStateList.valueOf(indicatorUtils.getIndicatorColor(product3)));

        // set onClickListener for store card view
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create new DetailActivity intent and pass the current store object to it
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("store", store);

                // start DetailActivity intent
                context.startActivity(intent);
            }
        });

        return view;
    }
}
