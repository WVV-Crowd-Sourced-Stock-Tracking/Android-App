package de.whatsLeft.ui.stores;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import de.whatsLeft.R;
import de.whatsLeft.store.Product;

public class IndicatorUtils {

    private Context context;

    /**
     * Constructor
     * @param context Context to access project colors
     */
    public IndicatorUtils(Context context) {
        this.context = context;
    }

    /**
     * @return resourceID resource id representing the indicator color for availability
     * @since 1.0.0
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public int getIndicatorColor(Product product) {
        int availability = product.getAvailability();
        if (availability >= 0 && availability < 34) return context.getColor(R.color.holoRedDark);
        else if (availability > 33 && availability < 67)  return context.getColor(R.color.holoOrangeDark);
        else if (availability > 66 && availability < 101) return context.getColor(R.color.holoGreenLight);
        else return context.getColor(R.color.darkerGray);
    }

    /**
     * @return indicatorText text from resource string file for text indicator representing availability
     * @since 1.0.0
     */
    public String getIndicatorText(Product product) {
        int availability = product.getAvailability();
        if (availability >= 0 && availability < 34) return context.getString(R.string.empty);
        else if (availability > 33 && availability < 67) return context.getString(R.string.less);
        else if (availability > 66 && availability < 101) return context.getString(R.string.available);
        else return context.getString(R.string.no_stock_available);
    }

}
