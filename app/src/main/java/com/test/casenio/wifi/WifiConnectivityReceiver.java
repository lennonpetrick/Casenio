package com.test.casenio.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class WifiConnectivityReceiver extends BroadcastReceiver {

    private Handler mTimeoutHandler;
    private Runnable mTimeoutRunnable;
    private ConnectivityListener mConnectivityListener;
    private WifiHelper mHelper;

    public WifiConnectivityReceiver(WifiHelper wifiHelper, ConnectivityListener listener) {
        mConnectivityListener = listener;
        mHelper = wifiHelper;
    }

    public void setTimeout(long millis) {
        mTimeoutHandler = new Handler();
        mTimeoutRunnable = () -> {
            if (!mHelper.isConnected() || !mHelper.isConnectedToWifi()) {
                mConnectivityListener.connectionTimeout();
            }

            mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
        };

        mTimeoutHandler.postDelayed(mTimeoutRunnable, millis);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mHelper.isConnected()) {
            if (mHelper.isConnectedToWifi()) {
                mTimeoutHandler.removeCallbacks(mTimeoutRunnable);
                mConnectivityListener.successfulConnected();
            } else {
                mHelper.disconnect();
            }
        }
    }

}
