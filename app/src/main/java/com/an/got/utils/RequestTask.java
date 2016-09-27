package com.an.got.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.an.got.GOTConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestTask extends AsyncTask<String, Void, Object> implements GOTConstants {

    private Context context;
    public RequestTask(Context context) {
        this.context = context;
    }

    public void downloadAssets(final String fileName) {
        try {
            String jsonResponse = null;
            if(!isValid()) {
                jsonResponse = getDefaultFile(fileName);
            } else {
                HttpURLConnection connection = createGETHttpsURLConnection(String.format("%s%s.json", RAW_URL, fileName));
                jsonResponse = readHttpResponse(connection, fileName);
            }
            storeAssets(fileName, jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void storeAssets(String fileName,
                            String responseString) {
        fileName = String.format(LOCALE_CACHE_PATH, context.getPackageName(), fileName);
        Utils.writeObjectToDisk(fileName, responseString);
    }

    @Override
    protected Object doInBackground(String... params) {
        try {
                downloadAssets(RAW_ONE);
                downloadAssets(RAW_TWO);
                downloadAssets(RAW_THREE);
                downloadAssets(RAW_FOUR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private boolean isValid() {
        return Utils.isNetworkAvailable(context);
    }

    private HttpURLConnection createGETHttpsURLConnection(String urlString)
            throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        return connection;
    }

    private String readHttpResponse(HttpURLConnection connection, String fileName) throws Exception {
        StringBuffer sb = new StringBuffer();
        try {
            Integer responseCode = connection.getResponseCode();
            InputStream inputStream;

            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
            } else {
                return getDefaultFile(fileName);
            }
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        String stringResponse = sb.toString();
        return stringResponse;
    }

    private String getDefaultFile(String fileName) {
        return Utils.getJSONStringFromRaw(context, context.getResources().getIdentifier(fileName,  "raw", context.getPackageName()));
    }
}
