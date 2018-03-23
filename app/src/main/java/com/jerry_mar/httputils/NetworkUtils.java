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
import java.util.Map;
import java.util.Set;

public class NetworkUtils extends BroadcastReceiver {
    public static final int TYPE_NONE = -1;
    public static final int TYPE_WIFI = ConnectivityManager.TYPE_WIFI;
    public static final int TYPE_MOBILE = ConnectivityManager.TYPE_MOBILE;

    private static Map<Context, BroadcastReceiver> receiverMaps;
    private static Map<Context, OnNetworkListener> listenerMaps;

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

    public static void registerReceiver(Context context, OnNetworkListener listener) {
        if(receiverMaps == null) {
            receiverMaps = new HashMap<>();
            listenerMaps = new HashMap<>();
        }

        IntentFilter intent = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        NetworkUtils receiver = new NetworkUtils();
        receiverMaps.put(context, receiver);
        listenerMaps.put(context, listener);
        context.registerReceiver(receiver, intent);
    }

    public static void unregisterReceiver(Context context) {
        BroadcastReceiver receiver = receiverMaps.remove(context);
        listenerMaps.remove(context);
        context.unregisterReceiver(receiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int type = getConnectedType(context);
        execute(type);
    }

    private void execute(int type) {
        Set<Context> set = listenerMaps.keySet();
        Iterator<Context> iterator = set.iterator();
        while (iterator.hasNext()) {
            Context context = iterator.next();
            OnNetworkListener listener = listenerMaps.get(context);
            listener.OnNetworkListener(type);
        }
    }

    public static interface OnNetworkListener {
        void OnNetworkListener(int type);
    }
}
