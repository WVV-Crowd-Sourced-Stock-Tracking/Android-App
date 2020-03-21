package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;
import java.util.Objects;

import de.nivram710.crowd_stock_supermarket.MainActivity;
import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.store.Product;
import de.nivram710.crowd_stock_supermarket.store.Store;

public class MarketsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mGoogleMap;
    private MapView mapView;
    private View mView;

    private MarketsViewModel marketsViewModel;
    private ListView listView;
    private ArrayList<Store> stores = new ArrayList<>();

    Store rewe = new Store(0, "Rewe", new Product[]{new Product(0, "Milch", "", 2), new Product(1, "Brot", "", 3), new Product(3, "Toilettenpapier", "", 0)}, false);
    Store lidl = new Store(1, "Lidl", new Product[]{new Product(0, "Milch", "", 0), new Product(1, "Brot", "", 0), new Product(3, "Toilettenpapier", "", 0)}, false);
    Store aldi = new Store(2, "Aldi", new Product[]{new Product(0, "Milch", "", 2), new Product(1, "Brot", "", 2), new Product(3, "Toilettenpapier", "", 3)}, false);


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_markets, container, false);
        listView = mView.findViewById(R.id.store_list_view);

        stores.add(rewe);
        stores.add(lidl);
        stores.add(aldi);

        RCCAdapter adapter = new RCCAdapter(getContext(), stores);

        listView.setAdapter(adapter);
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = mView.findViewById(R.id.map);
        if(mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(Objects.requireNonNull(getContext()));

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

}
