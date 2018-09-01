package com.test.casenio.wifi;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.lang.ref.WeakReference;

public class WifiHelper {

    private static WifiHelper INSTANCE;

    private WeakReference<Context> mWeakContext;
    private WifiManager mWifiManager;
    private WifiConnectivityReceiver mReceiver;
    private String mSSID;

    private WifiHelper(Context context) {
        mWifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    public synchronized static WifiHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new WifiHelper(context);
        }

        INSTANCE.setContext(context);
        return INSTANCE;
    }

    public synchronized static void destroy() {
        INSTANCE.unregisterReceiver();
        INSTANCE = null;
    }

    private void setContext(Context context) {
        mWeakContext = new WeakReference<>(context);
    }

    public void connectToWifi(String ssid, String password,
                              long timeout, ConnectivityListener listener) {
        WifiConfiguration config = createConfiguration(ssid, password);
        final int networkId = mWifiManager.addNetwork(config);
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(networkId, true);
        mWifiManager.reconnect();

        registerReceiver(timeout, listener);
    }

    private WifiConfiguration createConfiguration(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = mSSID = String.format("\"%s\"", ssid);
        config.preSharedKey = String.format("\"%s\"", password);
        config.hiddenSSID = true;
        return config;
    }

    private void registerReceiver(long timeout, final ConnectivityListener listener) {
        Context context = mWeakContext.get();
        if (context == null)
            return;

        mReceiver = new WifiConnectivityReceiver(this,
                new ConnectivityListener() {
                    @Override
                    public void successfulConnected() {
                        unregisterReceiver();
                        listener.successfulConnected();
                    }

                    @Override
                    public void connectionTimeout() {
                        unregisterReceiver();
                        disconnect();
                        listener.connectionTimeout();
                    }
                });

        mReceiver.setTimeout(timeout);
        context.registerReceiver(mReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void unregisterReceiver() {
        Context context = mWeakContext.get();
        if (context == null || mReceiver == null)
            return;

        context.unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    public boolean isWifiEnable() {
        return mWifiManager.isWifiEnabled();
    }

    public void setWifiEnable(boolean param) {
        mWifiManager.setWifiEnabled(param);
    }

    public boolean isConnected() {
        Context context = mWeakContext.get();
        if (context == null)
            return false;

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null)
            return false;

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public boolean isConnectedToWifi() {
        final String connectedSSID = mWifiManager
                .getConnectionInfo()
                .getSSID();
        return connectedSSID.equals(mSSID);
    }

    public void disconnect() {
        mWifiManager.disconnect();
    }

}
