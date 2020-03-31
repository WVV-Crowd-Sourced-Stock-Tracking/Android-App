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
 * This class is responsible for the communication with the REST backend
 *
 * @since 1.0.0
 * @author Marvin Jütte
 * @version 1.0
 */
public class CallAPI extends AsyncTask<String, String, String> {

    private static final String REQUEST_METHOD = "POST";
    private static final int READ_TIMEOUT = 30000;
    private static final int CONNECTION_TIMEOUT = 30000;

    private HttpURLConnection connection;

    private static final String TAG = "CallAPI";
    
    @Override
    protected String doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: called");

        // first parameter is request url
        String requestUrlString = strings[0];

        // second parameter is input string (best case the string is a json object)
        String data = strings[1];

        // default successful is failed
        String result = "failed";

        // log request url and input data
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

            // add input data to connection
            OutputStreamWriter streamWriter = new OutputStreamWriter(connection.getOutputStream());
            streamWriter.write(data);
            streamWriter.flush();

            // connect to url
            connection.connect();

            // save response from backend
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            // read response from backend
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // close reader
            reader.close();

            // store everything in result string
            result = stringBuilder.toString();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // disconnect from backend
            connection.disconnect();
        }

        // log if communication to backend was successful and the backend's response
        Log.i(TAG, "doInBackground: result: " + result);
        return result;
    }
}
