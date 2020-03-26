package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.store.Product;

public class RVPEAdapter extends BaseAdapter {

    private static final String TAG = "RVPEAdapter";

    private Context context;
    private ArrayList<Product> products;

    RVPEAdapter(Context context, ArrayList<Product> products) {
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
        view = View.inflate(context, R.layout.product_layout_edit, null);

        Log.d(TAG, "getView: test");
        
        final TextView textViewName = view.findViewById(R.id.text_view_product_name);
        textViewName.setText(products.get(position).getName());

        final Product product = products.get(position);
        Log.d(TAG, "getView: product: " + product);

        RadioGroup radioGroupStock = view.findViewById(R.id.radio_group_stock);
        final View finalView = view;
        radioGroupStock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {

                View radioButton = radioGroup.findViewById(checkedID);
                int radioID = radioGroup.indexOfChild(radioButton);

                TextView textViewProductName = finalView.findViewById(R.id.text_view_product_name);
                String productName = (String) textViewProductName.getText();
                Log.d(TAG, "onCheckedChanged: productName: " + productName);

                if(radioID == 0) {
                    for(Product product1 : products) {
                        if(product1.getName().equals(productName)){
                            product1.setAvailability(0);
                            Log.d(TAG, "onCheckedChanged: updated product: " + product1);
                            break;
                        }
                    }
                } else if(radioID == 1) {
                    for(Product product1 : products) {
                        if(product1.getName().equals(productName)){
                            product1.setAvailability(50);
                            Log.d(TAG, "onCheckedChanged: updated product: " + product1);
                            break;
                        }
                    }
                } else if(radioID == 2) {
                    for(Product product1 :products) {
                        if(product1.getName().equals(productName)) {
                            product1.setAvailability(100);
                            Log.d(TAG, "onCheckedChanged: updated product: " + product1);
                            break;
                        }
                    }
                }
            }
        });

        // get all radio buttons from view
        RadioButton radioButtonEmpty = view.findViewById(R.id.radio_button_empty);
        RadioButton radioButtonLess = view.findViewById(R.id.radio_button_less);
        RadioButton radioButtonEnough = view.findViewById(R.id.radio_button_enough);

        // set pre selection based on previous product availability
        if (product.getAvailability() > 100) {
            radioButtonEmpty.setChecked(false);
            radioButtonLess.setChecked(false);
            radioButtonEnough.setChecked(false);
        } else if(product.getAvailability() >= 0 && product.getAvailability() <= 33) {
            radioButtonEmpty.setChecked(true);
        } else if (product.getAvailability() >= 33 && product.getAvailability() <= 66) {
            radioButtonLess.setChecked(true);
        } else {
            radioButtonEnough.setChecked(true);
        }

        return view;
    }
}
