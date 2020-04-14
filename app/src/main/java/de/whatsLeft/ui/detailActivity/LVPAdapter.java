package de.whatsLeft.ui.detailActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import de.whatsLeft.R;
import de.whatsLeft.store.Product;
import de.whatsLeft.ui.IndicatorUtils;

/**
 * LVPAdapter --> ListViewProductsAdapter
 * Class to manage the products in DetailActivity in view mode
 *
 * @see DetailActivity
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class LVPAdapter extends BaseAdapter {

    private static final String TAG = "LVPAdapter";

    private Context context;
    private ArrayList<Product> products;

    /**
     * Constructor
     *
     * @param context context to access view
     * @param products array list containing all products to display them in list
     * @since 1.0.0
     */
    LVPAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.product_layout_view, null);

        // get current product
        Product product = products.get(position);

        // display product name with emoticon if there is one
        TextView textViewName = view.findViewById(R.id.text_view_product_name);
        String productName = !product.getEmoticon().equals("null") ? product.getEmoticon() + " " + product.getName() : product.getName();
        textViewName.setText(productName);

        // log product
        Log.d(TAG, "getView: product: " + product);

        // get indicator text views
        TextView colorIndicator = view.findViewById(R.id.color_indicator_product);
        TextView textViewIndicator = view.findViewById(R.id.text_view_product_text_indicator);

        // create new IndicatorUtils object
        IndicatorUtils indicatorUtils = new IndicatorUtils(context);

        // set values for indicator text view
        colorIndicator.setBackgroundTintList(ColorStateList.valueOf(indicatorUtils.getIndicatorColor(product)));
        textViewIndicator.setText(indicatorUtils.getIndicatorText(product));

        return view;
    }
}
