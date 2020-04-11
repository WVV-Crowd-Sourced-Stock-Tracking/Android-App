package de.whatsLeft.connectivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.whatsLeft.R;
import de.whatsLeft.store.Product;
import de.whatsLeft.store.Store;

/**
 * Child class of APIRequest; responsible to upload edited product stock
 *
 * @author Marvin Jütte
 * @version 1.0
 * @see APIRequest
 * @since 1.0.0
 */
public class TransmitProductStock extends APIRequest {

    @SuppressLint("StaticFieldLeak")
    private Context context;

    /**
     * Constructor
     *
     * @param context Context to access project strings
     * @param store   Store to get store id and it's products
     * @since 1.0.0
     */
    public TransmitProductStock(Context context, Store store) {
        super("/market/transmit", createInputJSONObject(store).toString());
        this.context = context;
    }

    /**
     * Creates and input json object
     *
     * @param store Store object to get store id and it's products
     * @return inputJsonObject containing all updates
     */
    private static JSONObject createInputJSONObject(Store store) {
        try {

            // create new empty input json object
            JSONObject inputJsonObject = new JSONObject();

            // create new product json array to store every update
            JSONArray updateJsonArray = new JSONArray();

            // for each product check if there is data
            for (Product product : store.getProducts()) {
                if (product.getAvailability() < 101 && product.getAvailability() > 0) {

                    // create update json object
                    JSONObject updateJsonObject = new JSONObject();
                    updateJsonObject.put("market_id", store.getId());
                    updateJsonObject.put("product_id", product.getId());
                    updateJsonObject.put("availability", product.getAvailability());

                    // add current product update to updates array
                    updateJsonArray.put(updateJsonObject);

                }
            }

            // add updates array to input json object
            inputJsonObject.put("bulk", updateJsonArray);

            // return input json object
            return inputJsonObject;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // if an error occurred return an empty json object
        return new JSONObject();
    }

    @Override
    protected void onPostExecute(String result) {

        try {
            // transform result String to json object
            JSONObject resultJsonObject = new JSONObject(result);

            String success = resultJsonObject.getString("result");

            if (success.equals("success"))
                Toast.makeText(context, context.getString(R.string.transmit_successful), Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, context.getString(R.string.transmit_failed), Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            Toast.makeText(context, context.getString(R.string.transmit_failed), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
