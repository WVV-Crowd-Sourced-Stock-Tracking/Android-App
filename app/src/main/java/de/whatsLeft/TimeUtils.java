package de.whatsLeft;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Relevant for all Periods or Date calculation inside this app
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class TimeUtils {

    /**
     * Looks if there is a period for the current week day
     *
     * @param jsonPeriodsArray periods for the store
     * @return index of store period for current day if it found one; else -1
     * @since 1.0.0
     */
    static int findPeriodForCurrentDay(JSONArray jsonPeriodsArray) {
        // if there are periods loop through them
        for (int i = 0; i < jsonPeriodsArray.length(); i++) {
            try {

                // get openDayID from current periods item
                JSONObject jsonPeriodObject = jsonPeriodsArray.getJSONObject(i);
                int openDayID = jsonPeriodObject.getInt("open_day_id");

                // if there is an item for current day return
                if (openDayID == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) return i;

                // abort, if openDayID is bigger the current day id and return -1 because no period was found
                if (openDayID > Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) return -1;

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
    static Date generateDateFromPeriods(JSONObject jsonPeriodObject, boolean closingTime) {
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
}
