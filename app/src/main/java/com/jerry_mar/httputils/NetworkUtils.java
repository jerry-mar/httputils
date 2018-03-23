package com.jerry_mar.httputils;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetworkUtils extends BroadcastReceiver {
    public static final int TYPE_NONE = -1;
    public static final int TYPE_WIFI = ConnectivityManager.TYPE_WIFI;
    public static final int TYPE_MOBILE = ConnectivityManager.TYPE_MOBILE;

    private static List<Callback> listener;
    private static NetworkUtils instance;

    @SuppressLint("MissingPermission")
    public static boolean isNetworkConnected(Context context) {
        boolean result = false;

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                result = info.isAvailable();
            }
        }

        return result;
    }

    @SuppressLint("MissingPermission")
    public static boolean isNetworkConnected(Context context, int networkType) {
        boolean result = false;

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if(manager != null) {
            NetworkInfo info = manager.getNetworkInfo(networkType);
            if (info != null) {
                result = info.isAvailable();
            }
        }

        return result;
    }

    @SuppressLint("MissingPermission")
    public static int getConnectedType(Context context) {
        int type = TYPE_NONE;
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                type = info.getType();
            }
        }

        return type;
    }

    public static void register(Context context) {
        if (instance == null) {
            listener = new LinkedList<>();
            instance = new NetworkUtils();
            IntentFilter intent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(instance, intent);
        }
    }

    public static void unregister(Context context) {
        if (instance != null) {
            listener.clear();
            context.unregisterReceiver(instance);
            instance = null;
        }
    }

    public static void register(Callback c) {
        listener.add(c);
    }

    public static void unregister(Callback c) {
        listener.remove(c);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = getConnectedType(context);
        execute(type);
    }

    private void execute(int type) {
        Iterator<Callback> iterator = listener.iterator();
        while (iterator.hasNext()) {
            Callback c = iterator.next();
            c.execute(type);
        }
    }

    public static interface Callback {
        void execute(int type);
    }
}
