package de.whatsLeft.connectivity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class is the super class and it defines what to do in background
 *
 * @author Marvin Jütte
 * @version 1.0
 * @since 1.0.0
 */
public class APIRequest extends AsyncTask<String, String, String> {

    // private static final String REQUEST_URL = "https://wvvcrowdmarket.herokuapp.com/ws/rest";
    private static final String CONNECTION_URL = "https://wvv2.herokuapp.com/ws/rest";

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 30_000;
    private static final int CONNECTION_TIMEOUT = 30_000;
    private static final String TAG = "newAPIRequest";
    private HttpURLConnection connection;
    private String requestUrlString;
    private String inputData;

    /**
     * Constructor
     *
     * @param requestUrlString String containing an attachment for connection url e.g. /market/scrape
     * @param inputData        String of a json object containing input data like coordinates of you location
     * @since 1.0.0
     */
    APIRequest(String requestUrlString, String inputData) {
        this.requestUrlString = requestUrlString;
        this.inputData = inputData;
    }

    /**
     * Constructor
     *
     * @param requestUrlString String containing an attachment for connection url e.g. /market/scrape
     * @since 1.0.0
     */
    APIRequest(String requestUrlString) {
        this.requestUrlString = requestUrlString;
    }

    @Override
    protected String doInBackground(String[] strings) {
        Log.d(TAG, "doInBackground: called");

        // default result is failed
        String result = "failed";

        // log url and input data
        Log.i(TAG, "doInBackground: requestURL: " + CONNECTION_URL + requestUrlString);
        Log.i(TAG, "doInBackground: inputData: " + inputData);

        try {
            // create connection object
            URL requestURL = new URL(CONNECTION_URL + requestUrlString);
            connection = (HttpURLConnection) requestURL.openConnection();

            // setup connection
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            // prepare connection for post request
            connection.setDoInput(true);
            connection.setDoOutput(inputData != null);
            connection.setUseCaches(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // if there is input data add it to connection
            if (inputData != null) {
                OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
                streamWriter.write(inputData);
                streamWriter.flush();
            }

            // connect to api
            connection.connect();

            // save response from server
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // read response from server
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // close reader
            reader.close();

            // store output in result string
            result = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // disconnect from api
            connection.disconnect();
        }

        // log result to show whether communication was successful or not
        Log.i(TAG, "doInBackground: result: " + result);
        return result;
    }
}
