package de.nivram710.crowd_stock_supermarket.connectivity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPGetRequest extends AsyncTask {

    private static final String REQUEST_METHOD = "GET";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private static final String TAG = "HTTPGetRequest";

    private HttpURLConnection connection;

    @Override
    protected Object doInBackground(Object[] objects) {
        String stringRequestUrl = (String) objects[0];
        Log.d(TAG, "doInBackground: stringRequestURL: " + stringRequestUrl);
        String result = "test";

        try {
            // create connection object
            URL requestUrl = new URL(stringRequestUrl);
            connection = (HttpURLConnection) requestUrl.openConnection();

            // setup connection
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            // connect to url
            connection.connect();

            // create InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            System.out.println(streamReader);

            // create a buffered reader and StringBuilder to save input stream in string object
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();

            // loop through the line and add them to string builder object
            for(String inputLine = reader.readLine(); inputLine != null; inputLine=reader.readLine()) {
                Log.d(TAG, "doInBackground: inputLine: " + inputLine);
                stringBuilder.append(inputLine);
            }

            // close input stream  or buffered reader
            reader.close();
            streamReader.close();

            // save input from string builder in result string
            result = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        Log.d(TAG, "doInBackground: result: " + result);
        return result;
    }
}
