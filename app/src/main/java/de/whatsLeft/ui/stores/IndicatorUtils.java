package de.whatsLeft.ui.stores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import de.whatsLeft.R;
import de.whatsLeft.TimeUtils;
import de.whatsLeft.store.Product;
import de.whatsLeft.store.Store;

/**
 * Class that help to get correct indicator color or text
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.1
 */
public class IndicatorUtils {

    private Context context;

    /**
     * Constructor
     * @param context Context to access project colors
     * @since 1.0.0
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

    /**
     * Returns color to indicate with color whether the store is open or not
     *
     * @param store Store object to access opening and closingDate
     * @return colorId
     * @since 1.0.0
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public int getStoreOpenTextColor(Store store) {
        // get current date
        Date date = new Date();

        // if there is no date store has open all day --> return green
        if(store.getOpeningDate() == null) return context.getColor(R.color.holoGreenLight);

        // if current date before store opens or after store closed return red color
        if (date.before(store.getOpeningDate()) || date.after(store.getClosingDate()))
            return context.getColor(R.color.holoRedDark);

        // if store is shortly before closing return orange color
        if (TimeUtils.getTimeDifference(date, store.getClosingDate()) <= 1)
            return context.getColor(R.color.holoOrangeDark);

        // else return green color
        else return context.getColor(R.color.holoGreenLight);
    }

    /**
     * Creates and formats the text to display store open status
     *
     * @param store Store object to access opening and close time
     * @return openText a String that is display to show user information about opening times
     * @since 1.0.0
     */
    @SuppressLint("SimpleDateFormat")
    public String getStoreOpenText(Store store) {

        // get current Date
        Date date = new Date();

        // if store is always open return open
        if(store.getOpeningDate() == null) return context.getString(R.string.store_is_open);

        if (date.before(store.getOpeningDate())) {
            // add pre text to indicate that store is not open yet
            String openText = context.getString(R.string.opens_at);

            // get time when store opens and add it to string
            openText += " " + new SimpleDateFormat("HH:mm").format(store.getOpeningDate());
            return openText;

        } else if(date.after(store.getClosingDate())) {
            // if current time after closing time display closed
            return context.getString(R.string.store_is_closed);
        } else {

            // generate empty open text string
            String openText;

            int timeDifference = TimeUtils.getTimeDifference(date, store.getClosingDate());

            if (timeDifference <= 1) {
                // if difference between closing time and current time is less then 1h display closing at <ClosingTime>
                openText = context.getString(R.string.store_is_closing_at);

                // get closing time and add it to openText string
                openText += " " + new SimpleDateFormat("HH:mm").format(store.getClosingDate());

            } else {
                // else display open
                openText = context.getString(R.string.store_is_open);
            }
            return openText;
        }
    }
}
