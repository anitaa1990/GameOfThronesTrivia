package com.an.got.utils;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.an.got.GOTConstants;
import com.an.got.callbacks.OnSurveyListener;
import com.an.got.model.Question;
import com.an.got.model.Survey;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Utils implements GOTConstants {

    public static String getJSONStringFromRaw(Context context, int rawId) {

        InputStream content = context.getResources().openRawResource(rawId);
        BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
        String respString = "";
        try {
            String s = "";
            while ((s = buffer.readLine()) != null) {
                respString += s;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respString;
    }

    public static void writeObjectToDisk(String fileName, Object object) {
        ObjectUtil objDataStream = new ObjectUtil();
        objDataStream.writeObjects(object,fileName);
    }

    public static Object readObjectFromDisk(String fileName) {
        ObjectUtil objDataStream = new ObjectUtil();
        return objDataStream.readObjects(fileName);
    }

    public static long getScoreForQuestion(int timeRemaining, int guesses) {
        long score =  guesses * timeRemaining;
        return score;
    }

    public static void getSurveyFromFile(final Context context,
                                         final String fileName,
                                         final OnSurveyListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = String.format(LOCALE_CACHE_PATH, context.getPackageName(), fileName);
                String responseString = (String) Utils.readObjectFromDisk(name);
                Survey survey = new Gson().fromJson(responseString, Survey.class);
                listener.onFetchSurvey(survey);
            }
        }).start();
    }

    private static final float DENSITY = Resources.getSystem().getDisplayMetrics().density;
    private static final Canvas sCanvas = new Canvas();

    public static int dp2Px(int dp) {
        return Math.round(dp * DENSITY);
    }

    public static Bitmap createBitmapFromView(View view) {
        if (view instanceof ImageView) {
            Drawable drawable = ((ImageView) view).getDrawable();
            if (drawable != null && drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            }
        }
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null) {
            synchronized (sCanvas) {
                Canvas canvas = sCanvas;
                canvas.setBitmap(bitmap);
                view.draw(canvas);
                canvas.setBitmap(null);
            }
        }
        return bitmap;
    }

    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
            return null;
        }
    }

    public static int getImageId(Context context, String resourceName) {
        try {
            int nameResourceID =  context.getResources().getIdentifier(resourceName , "mipmap", context.getPackageName());;
            return nameResourceID;
        } catch (Exception e) {
            e.printStackTrace();
            int nameResourceID =  context.getResources().getIdentifier(resourceName , "mipmap", context.getPackageName());;
            return nameResourceID;
        }
    }

    public static boolean isNetworkAvailable(Context mContext) {
        if(!isPermissionGranted(mContext)) return false;
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isPermissionGranted(Context context) {
        boolean hasPermission = checkPermissions(context, Manifest.permission.ACCESS_NETWORK_STATE);
        if(!hasPermission) return hasPermission;
        hasPermission = checkPermissions(context, Manifest.permission.INTERNET);
        if(!hasPermission) return hasPermission;
        hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED;
        if(!hasPermission) return hasPermission;
        hasPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED;
        return hasPermission;
    }

    public static Boolean checkPermissions(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        return (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED);
    }
}
