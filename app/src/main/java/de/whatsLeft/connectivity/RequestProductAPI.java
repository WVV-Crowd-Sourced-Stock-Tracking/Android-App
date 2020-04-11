package de.whatsLeft.connectivity;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.whatsLeft.MainActivity;
import de.whatsLeft.store.Product;

/**
 * Child class of APIRequest; responsible for getting all available products
 *
 * @author Marvin Jütte
 * @version 1.0
 * @see APIRequest
 * @since 1.0.0
 */
public class RequestProductAPI extends APIRequest {

    private static final String TAG = "newRequestProductAPI";

    /**
     * Constructor
     *
     * @since 1.0.0
     */
    public RequestProductAPI() {
        super("/product/scrape");
    }

    /**
     * Generates a product object from the given json object
     *
     * @param productJsonObject JsonObject; a json object containing all product information
     * @return product Product; the product object which attributes are equal to the json object's ones
     * @since 1.0.0
     */
    private static Product generateProductFromJsonObject(JSONObject productJsonObject) {
        try {
            // get all attributes from json object
            int id = productJsonObject.getInt("product_id");
            String name = productJsonObject.getString("product_name");
            String emoticon = productJsonObject.getString("emoji");

            // set availability of product
            int availability;

            // check if product json object has a availability field
            if (productJsonObject.has("availability")) {
                // if it has one; the availability will be set to the json object's value
                availability = productJsonObject.getInt("availability");
            } else {
                // else it will be 101 to indicate that there are no further information about it's availability
                availability = 101;
            }

            // create new product object
            Product product = new Product(id, name, emoticon, availability);

            Log.d(TAG, "generateProductFromJsonObject: new Product: " + product);

            return product;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // if creation failed return null
        return null;
    }

    @Override
    protected void onPostExecute(String result) {

        // create empty products list
        ArrayList<Product> products = new ArrayList<>();

        // create highestProductID variable to get the highestProductID
        int highestProductID = 0;

        try {
            // transform result string to json object
            JSONObject resultJsonObject = new JSONObject(result);

            // get products array from backend response
            JSONArray productsJsonArray = resultJsonObject.getJSONArray("product");

            // loop through all products
            for (int i = 0; i < productsJsonArray.length(); i++) {

                // get json product object from array
                JSONObject productJsonObject = productsJsonArray.getJSONObject(i);

                // generate Product object
                Product product = generateProductFromJsonObject(productJsonObject);

                // update highestProductID if necessary
                assert product != null;
                highestProductID = Math.max(highestProductID, product.getId());

                // add product to products array list
                products.add(product);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MainActivity.availableProducts = products;
        MainActivity.highestID = highestProductID;
    }
}
