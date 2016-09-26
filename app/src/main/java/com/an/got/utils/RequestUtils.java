package com.an.got.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.an.got.GOTConstants;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

public class RequestUtils extends AsyncTask implements GOTConstants {

    private Context context;
    public RequestUtils(Context context) {
        this.context = context;
    }

    public void downloadAssets() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                downloadAssets(RAW_ONE);
                downloadAssets(RAW_TWO);
                downloadAssets(RAW_THREE);
                downloadAssets(RAW_FOUR);
            }
        }).start();
    }

    public void downloadAssets(final String fileName) {
        Ion.with(context)
                .load(String.format("%s%s", RAW_URL, fileName))
                .setLogging("MyLogs", Log.DEBUG)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        String responseString = (result.getHeaders().code() == 200) ? result.getResult() :
                                Utils.getJSONStringFromRaw(context, context.getResources().getIdentifier(fileName,
                                        "raw", context.getPackageName()));
                        storeAssets(fileName, responseString);
                    }
                });
    }

    public void storeAssets(String fileName,
                            String responseString) {
        fileName = String.format(LOCALE_CACHE_PATH, context.getPackageName(), fileName);
        Utils.writeObjectToDisk(fileName, responseString);
    }
}
