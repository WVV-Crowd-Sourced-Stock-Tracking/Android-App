package de.whatsLeft;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.whatsLeft.store.Product;

/**
 * Relevant for static methods like processing dates or generating product lists
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.1
 */
public class FormatUtils {

    private static final String TAG = "FormatUtils";

    /**
     * Looks if there is a period for the current week day
     *
     * @param jsonPeriodsArray periods for the store
     * @return index of store period for current day if it found one; else -1
     * @since 1.0.0
     */
    public static int findPeriodForCurrentDay(JSONArray jsonPeriodsArray) {
        // if there are periods loop through them
        for (int i = 0; i < jsonPeriodsArray.length(); i++) {
            try {

                // get openDayID from current periods item
                JSONObject jsonPeriodObject = jsonPeriodsArray.getJSONObject(i);
                int openDayID = jsonPeriodObject.getInt("open_day_id");

                // if there is an item for current day return
                if (openDayID == (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)) return i;

                // abort, if openDayID is bigger the current day id and return -1 because no period was found
                if (openDayID > (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)) return -1;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // if there is no period corresponding to out current day return -1
        return -1;
    }

    /**
     * Generates java Date object from jsonPeriodObject delivered by backend
     *
     * @param jsonPeriodObject JsonObject containing period information for current day
     * @param closingTime boolean value to indicate if current date shall represent closing or opening time
     * @return date if closingTime --> closingDate; else openingDate
     * @since 1.0.0
     */
    public static Date generateDateFromPeriods(JSONObject jsonPeriodObject, boolean closingTime) {
        try {
            String time;
            if (closingTime) time = jsonPeriodObject.getString("close_time");
            else time = jsonPeriodObject.getString("open_time");

            String[] timeSplitted = time.split(":");
            int hour = Integer.parseInt(timeSplitted[0]);
            int minute = Integer.parseInt(timeSplitted[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            return calendar.getTime();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * Calculates the difference between to dates
     *
     * @param date1 date1 is the comparing date
     * @param date2 date2 is the date date1 is compared to
     * @return difference Difference between two dates in hours
     * @since 1.0.0
     */
    public static int getTimeDifference(Date date1, Date date2) {
        // calculate difference between dates
        long diffInMillis = Math.abs(date2.getTime() - date1.getTime());

        // return the to hours converted time difference
        return (int) TimeUnit.HOURS.convert(diffInMillis, TimeUnit.MILLISECONDS);

    }

    /**
     * Generates a products array list with product objects from a products json array
     *
     * @param jsonProductsArray containing all products
     * @return products array list containing all products for the store
     * @since 1.0.0
     */
    public static ArrayList<Product> generateProductsList(JSONArray jsonProductsArray) {
        // create empty products list
        ArrayList<Product> products = new ArrayList<>();

        // create array to indicate whether for the given product id a product is available in store
        boolean[] productsInList = new boolean[MainActivity.highestID + 1];
        Arrays.fill(productsInList, Boolean.FALSE);

        try {
            for (int i = 0; i < jsonProductsArray.length(); i++) {
                JSONObject productJsonObject = jsonProductsArray.getJSONObject(i);

                // get all relevant attributes from json object
                int id = productJsonObject.getInt("product_id");
                String name = productJsonObject.getString("product_name");
                String emoticon = productJsonObject.getString("emoji");
                int availability = productJsonObject.getInt("availability");


                // create product and store it in array list
                Product product = new Product(id, name, emoticon, availability);
                products.add(product);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // store true in array where index equals product id to indicate that product is already in store
        for (Product product : products) {
            productsInList[product.getId()] = true;
        }

        // add all missing products to store
        // loop through all available Products
        for (Product product : MainActivity.availableProducts) {

            // and check if the product is already in product list
            if (!productsInList[product.getId()]) {
                try {
                    // if not clone the current product and add it to products list
                    products.add((Product) product.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d(TAG, "generateProductsList: products: " + products);

        return products;
    }

    /**
     * If the distance is less then 1000 method returns distance in meters + unit; else it return
     * distance in kilometers with unit
     *
     * @param distance double; distance attribute returned by backend
     * @return formattedDistance String; distance with unit added
     * @since 1.0.0
     */
    public static String getFormattedDistance(double distance) {

        // create string object that will be returned later
        String distanceString;

        // check if rounded distance is greater then 1000
        if ((int) distance > 1000) {

            // if distance is greater then 1000m convert distance into kilometers
            double distanceTemp = distance / 1000;
            double distanceInKm = (double) Math.round(distanceTemp * 100) / 100;
            distanceString = distanceInKm + "km";

        } else {
            // if not display distance in meters
            distanceString = (int) distance + "m";
        }

        // return distance as a string
        return distanceString;
    }
}
