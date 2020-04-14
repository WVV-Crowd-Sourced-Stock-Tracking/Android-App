package de.whatsLeft.ui.detailActivity;

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

import de.whatsLeft.R;
import de.whatsLeft.store.Product;

/**
 * LVPEAdapter --> ListViewProductsEditorAdapter
 * Class to manage the products in DetailActivity in edit mode
 *
 * @see DetailActivity
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class LVPEAdapter extends BaseAdapter {

    private static final String TAG = "RVPEAdapter";

    private Context context;
    private ArrayList<Product> products;

    /**
     * Constructor
     *
     * @param context  context to access current view
     * @param products array list containing all products to add them to edit view
     * @since 1.0.0
     */
    LVPEAdapter(Context context, ArrayList<Product> products) {
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

        // get current product and print it in log
        final Product product = products.get(position);
        Log.d(TAG, "getView: product: " + product);

        // display product name + emoticon if there is one
        TextView textViewName = view.findViewById(R.id.text_view_product_name);
        String productName = !product.getEmoticon().equals("null") ? product.getEmoticon() + " " + product.getName() : product.getName();
        textViewName.setText(productName);

        // get radio group representing the item stock
        RadioGroup radioGroupStock = view.findViewById(R.id.radio_group_stock);
        final View finalView = view;

        // set on change listener for radio group
        radioGroupStock.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {

                // ger current radio button to get its ID
                View radioButton = radioGroup.findViewById(checkedID);
                int radioID = radioGroup.indexOfChild(radioButton);

                // log the name of the product being processed
                TextView textViewProductName = finalView.findViewById(R.id.text_view_product_name);
                String productName = (String) textViewProductName.getText();
                Log.d(TAG, "onCheckedChanged: productName: " + productName);
                Log.d(TAG, "onCheckedChanged: radioID: " + radioID);

                // find product in products list
                Product product1 = findProductInList(productName);

                // if product is unequal to null
                assert product1 != null;

                // update its availability
                switch (radioID) {
                    case 0:
                        // radioID 0 representing empty stock --> set availability to 0
                        product1.setAvailability(0);
                        break;
                    case 1:
                        // radioID 1 representing almost sold out --> set availability to 50
                        product1.setAvailability(50);
                        break;
                    case 2:
                        // radioID 2 representing available --> set availability to 100
                        product1.setAvailability(100);
                }
                Log.d(TAG, "onCheckedChanged: updated product: " + product1);
            }
        });

        // get all radio buttons from view
        RadioButton radioButtonEmpty = view.findViewById(R.id.radio_button_empty);
        RadioButton radioButtonLess = view.findViewById(R.id.radio_button_less);
        RadioButton radioButtonEnough = view.findViewById(R.id.radio_button_enough);

        // set pre selection based on previous product availability
        if (product.getAvailability() > 100) {
            // if there is no stock information do not preselect any button
            radioButtonEmpty.setChecked(false);
            radioButtonLess.setChecked(false);
            radioButtonEnough.setChecked(false);

        } else if (product.getAvailability() >= 0 && product.getAvailability() <= 33) {

            // if there is data available but indicating that the product is sold out
            // preselect empty radio button
            radioButtonEmpty.setChecked(true);
        } else if (product.getAvailability() >= 33 && product.getAvailability() <= 66) {

            // if there is data available but indicating that the product is rarely
            // preselect less radio button
            radioButtonLess.setChecked(true);
        } else {

            // if there is data available but indicating that the product is available
            // preselect available radio button
            radioButtonEnough.setChecked(true);
        }

        return view;
    }

    /**
     * Returns product base on textView representing product with its name and emoticon
     *
     * @param textViewText text of textView containing product name and emoticon
     * @return product if product was found in list else return null
     * @since 1.0.0
     */
    private Product findProductInList(String textViewText) {
        // split textViewText cause only the product name is needed
        String productName = textViewText.split(" ")[1].trim();

        // loop through all products and return the searched product
        for (Product product : products) {
            if (product.getName().equals(productName)) return product;
        }
        return null;
    }
}
