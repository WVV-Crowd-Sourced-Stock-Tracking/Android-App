package de.nivram710.crowd_stock_supermarket.ui.markets;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import de.nivram710.crowd_stock_supermarket.R;
import de.nivram710.crowd_stock_supermarket.store.Store;

public class RCCAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Store> stores;

    public RCCAdapter(Context context, ArrayList<Store> stores) {
        this.context = context;
        this.stores = stores;
    }

    @Override
    public int getCount() {
        return stores.size();
    }

    @Override
    public Object getItem(int position) {
        return stores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        view = View.inflate(context, R.layout.store_card, null);

        final Store store = stores.get(position);

        TextView textViewName = view.findViewById(R.id.text_view_store_name);
        textViewName.setText(store.getName());

        TextView textViewAddress = view.findViewById(R.id.text_view_store_address);
        String addressDistance = store.getAddress() + " - " + (int) store.getDistance() + "m";
        textViewAddress.setText(addressDistance);

        TextView textViewIsOpen = view.findViewById(R.id.text_view_is_open);
        if(store.isOpen()) {
            textViewIsOpen.setText(context.getString(R.string.store_is_open));
            textViewIsOpen.setTextColor(context.getColor(R.color.holoGreenLight));
        } else {
            textViewIsOpen.setText(context.getString(R.string.store_is_closed));
            textViewIsOpen.setTextColor(context.getColor(R.color.holoRedDark));
        }

        TextView indicator_milk = view.findViewById(R.id.color_indicator_milk);
        final TextView indicator_bread = view.findViewById(R.id.color_indicator_bread);
        TextView indicator_toilet_paper = view.findViewById(R.id.color_indicator_toilet_paper);
        if(store.getProducts().length >= 3) {
            switch (store.getProducts()[0].getAvailability()) {
                case 0:
                    indicator_milk.setBackground(context.getDrawable(R.drawable.circle_red));
                case 1:
                    indicator_milk.setBackground(context.getDrawable(R.drawable.circle_yellow));
                case 2:
                    indicator_milk.setBackground(context.getDrawable(R.drawable.circle_green));
                default:
                    indicator_milk.setBackground(context.getDrawable(R.drawable.circle_gray));
            }

            switch (store.getProducts()[1].getAvailability()) {
                case 0:
                    indicator_bread.setBackground(context.getDrawable(R.drawable.circle_red));
                case 1:
                    indicator_bread.setBackground(context.getDrawable(R.drawable.circle_yellow));
                case 2:
                    indicator_bread.setBackground(context.getDrawable(R.drawable.circle_green));
                default:
                    indicator_bread.setBackground(context.getDrawable(R.drawable.circle_gray));
            }

            switch (store.getProducts()[1].getAvailability()) {
                case 0:
                    indicator_toilet_paper.setBackground(context.getDrawable(R.drawable.circle_red));
                case 1:
                    indicator_toilet_paper.setBackground(context.getDrawable(R.drawable.circle_yellow));
                case 2:
                    indicator_toilet_paper.setBackground(context.getDrawable(R.drawable.circle_green));
                default:
                    indicator_toilet_paper.setBackground(context.getDrawable(R.drawable.circle_gray));
            }
        } else {
            indicator_milk.setBackground(context.getDrawable(R.drawable.circle_gray));
            indicator_bread.setBackground(context.getDrawable(R.drawable.circle_gray));
            indicator_toilet_paper.setBackground(context.getDrawable(R.drawable.circle_gray));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Store store = stores.get(position);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("id", store.getId());
                intent.putExtra("name", store.getName());
                intent.putExtra("address", store.getAddress());
                intent.putExtra("isOpen", store.isOpen());

                context.startActivity(intent);
            }
        });

        return view;
    }
}
