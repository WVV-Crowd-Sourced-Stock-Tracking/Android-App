package de.nivram710.crowd_stock_supermarket.connectivity;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class CallAPI extends AsyncTask<String, String, String> {

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 15000;
    private static final int CONNECTION_TIMEOUT = 15000;

    private HttpURLConnection connection;

    private static final String TAG = "CallAPI";
    
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: called");
        String requestUrlString = strings[0];
        String data = strings[1];
        String result = "failed";

        Log.i(TAG, "doInBackground: requestUrlString: " + requestUrlString);
        Log.i(TAG, "doInBackground: data: " + data);

        try {
            // create connection object
            URL requestUrl = new URL(requestUrlString);
            connection = (HttpURLConnection) requestUrl.openConnection();

            // setup connection
            connection.setRequestMethod(REQUEST_METHOD);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);

            // prepare connection for post request
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(data);
            streamWriter.flush();

            // connect to url
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            reader.close();
            result = stringBuilder.toString();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        Log.i(TAG, "doInBackground: result: " + result);
        return result;
    }
}
