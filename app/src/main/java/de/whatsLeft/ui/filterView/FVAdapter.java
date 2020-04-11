package de.whatsLeft.ui.filterView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;
import de.whatsLeft.store.Product;

public class FVAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Product> products;
    private ArrayList<Product> selectedProducts = new ArrayList<>();

    /**
     * Constructor
     *
     * @param context  Context to use layout inflater
     * @param products ArrayList containing all available Products
     * @since 1.0.0
     */
    FVAdapter(Context context, ArrayList<Product> products) {
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

    @SuppressLint("ViewHolder")
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.filter_view_product_item, null);

        // get current product
        final Product product = products.get(position);

        // display product name
        TextView textViewProductName = view.findViewById(R.id.text_view_product_name);
        String productNameWithEmoticon = product.getEmoticon() + " " + product.getName();
        textViewProductName.setText(productNameWithEmoticon);

        // get Checkbox and set onCheckedChangeListener
        final CheckBox checkBox = view.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                // if product is check add it to selected products list else remove it
                if (checked) selectedProducts.add(product);
                else selectedProducts.remove(product);
            }
        });

        // preselect products if user has already selected them
        checkBox.setChecked(MainActivity.selectedProducts.contains(product));

        return view;
    }

    /**
     * Deselects all checkboxes for the given list view
     *
     * @param listView which child views contains the checkboxes
     * @since 1.0.0
     */
    void removeAllChecks(ListView listView) {
        for (int i = 0; i < listView.getChildCount(); i++) {
            // get productItemView which contains checkbox
            View productItemView = listView.getChildAt(i);

            // get checkbox from productItemView and uncheck it
            CheckBox checkBox = productItemView.findViewById(R.id.checkBox);
            checkBox.setChecked(false);
        }

        // remove all items from selected list
        selectedProducts.clear();
    }

    /**
     * @return selectedProducts an ArrayList containing all selected products
     */
    ArrayList<Product> getSelectedProducts() {
        return selectedProducts;
    }
}
