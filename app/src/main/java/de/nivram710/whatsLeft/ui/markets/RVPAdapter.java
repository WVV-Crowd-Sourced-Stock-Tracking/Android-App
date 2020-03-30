package de.nivram710.whatsLeft.ui.markets;

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

import de.nivram710.whatsLeft.R;
import de.nivram710.whatsLeft.store.Product;

public class RVPAdapter extends BaseAdapter {

    private static final String TAG = "RVPAdapter";

    private Context context;
    private ArrayList<Product> products;

    RVPAdapter(Context context, ArrayList<Product> products) {
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
        Product product = products.get(position);

        TextView textViewName = view.findViewById(R.id.text_view_product_name);
        String productName = !product.getEmoticon().equals("null") ? product.getEmoticon() + " " + product.getName() : product.getName();
        textViewName.setText(productName);

        Log.d(TAG, "getView: product: " + product);

        // get indicator text views
        TextView colorIndicator = view.findViewById(R.id.color_indicator_product);
        TextView textViewIndicator = view.findViewById(R.id.text_view_product_text_indicator);

        // set values for indicator text view
        colorIndicator.setBackgroundTintList(ColorStateList.valueOf(getIndicatorColor(product.getAvailability())));
        textViewIndicator.setText(getIndicatorText(product.getAvailability()));

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getIndicatorColor(int availability) {
        if (availability >= 0 && availability < 34) {
            return context.getColor(R.color.holoRedDark);
        } else if (availability > 33 && availability < 67) {
            return context.getColor(R.color.holoOrangeDark);
        } else if (availability > 66 && availability < 101) {
            return context.getColor(R.color.holoGreenLight);
        } else {
            return context.getColor(R.color.darkerGray);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private String getIndicatorText(int availability) {
        if (availability > 0 && availability < 34) {
            return context.getString(R.string.empty);
        } else if (availability > 33 && availability < 67) {
            return context.getString(R.string.less);
        } else if (availability > 66 && availability < 101) {
            return context.getString(R.string.available);
        } else {
            return context.getString(R.string.no_stock_available);
        }
    }
}
