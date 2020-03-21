package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import de.nivram710.crowd_stock_supermarket.R;

public class marketsFragment extends Fragment {

    private marketsViewModel marketsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        marketsViewModel =
                ViewModelProviders.of(this).get(marketsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_markets, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        marketsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
