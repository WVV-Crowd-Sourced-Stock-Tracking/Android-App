package de.whatsLeft.ui.filterView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;

/**
 * Fragment to apply filter for store request
 * <p>Products in ListView are managed by FVAdapter</p>
 *
 * @author Marvin Jütte
 * @version 1.0
 * @see FVAdapter
 * @since 1.0.0
 */
public class FilterViewFragment extends Fragment {

    private static final String TAG = "FilterViewFragment";

    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_filter_view, container, false);

        ListView listViewFilterProduct = root.findViewById(R.id.list_view_filter_products);

        final FVAdapter adapter = new FVAdapter(getContext(), MainActivity.availableProducts);
        listViewFilterProduct.setAdapter(adapter);

        // get delete filter button and give it it's functionality
        Button buttonDeleteFilter = root.findViewById(R.id.button_delete_filter);
        buttonDeleteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get list view through root view
                View rootView = view.getRootView();
                ListView listView = rootView.findViewById(R.id.list_view_filter_products);

                // uncheck all checkboxes
                adapter.removeAllChecks(listView);

                // delete all selected Products from MainActivity
                MainActivity.selectedProducts.clear();
            }
        });

        // get apply filter button and pass wanted products to market fragment
        Button buttonApplyFilter = root.findViewById(R.id.button_apply_filter);
        buttonApplyFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // store the selected products in main activity
                MainActivity.selectedProducts = adapter.getSelectedProducts();
                Log.d(TAG, "onClick: MainActivity.selectedProducts: " + MainActivity.selectedProducts);

                // change back to market view
                Navigation.findNavController(view).navigate(R.id.navigation_markets);
            }
        });

        return root;
    }
}