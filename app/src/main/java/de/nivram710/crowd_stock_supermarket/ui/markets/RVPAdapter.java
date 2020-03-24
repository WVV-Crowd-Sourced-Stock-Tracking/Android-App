package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.store.Product;

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

        TextView textViewName = view.findViewById(R.id.text_view_product_name);
        textViewName.setText(products.get(position).getName());

        Product product = products.get(position);
        Log.d(TAG, "getView: product: " + product);

        RadioButton radioButton = view.findViewById(R.id.radio_button_status);
        if (product.getAvailability() > 100) {
            radioButton.setChecked(true);
            radioButton.setText(context.getString(R.string.no_stock_available));
            radioButton.setButtonTintList(ColorStateList.valueOf(context.getColor(R.color.very_dark_gray)));
        } else if(product.getAvailability() >= 0 && product.getAvailability() <= 33) {
            radioButton.setChecked(true);
            radioButton.setText(context.getString(R.string.sold_out));
        } else if (product.getAvailability() >= 33 && product.getAvailability() <= 66) {
            radioButton = view.findViewById(R.id.radio_button_status);
            radioButton.setChecked(true);
            radioButton.setButtonTintList(ColorStateList.valueOf(context.getColor(R.color.holoOrangeDark)));
            radioButton.setText(context.getString(R.string.almost_sold_out));
        } else {
            radioButton = view.findViewById(R.id.radio_button_status);
            radioButton.setChecked(true);
            radioButton.setButtonTintList(ColorStateList.valueOf(context.getColor(R.color.holoGreenLight)));
            radioButton.setText(context.getString(R.string.available));
        }

        return view;
    }
}
