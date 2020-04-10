package de.whatsLeft.ui.filterView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import de.whatsLeft.MainActivity;
import de.whatsLeft.R;

public class FilterViewFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
            }
        });

        return root;
    }
}