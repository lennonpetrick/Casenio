package com.test.casenio.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler

class WifiConnectivityReceiver(private val helper: WifiHelper,
                               private val connectivityListener: ConnectivityListener)
    : BroadcastReceiver() {

    private var timeoutHandler: Handler? = null
    private var timeoutRunnable: Runnable? = null

    fun timeout(millis: Long) {
        timeoutHandler = Handler()
        timeoutRunnable = Runnable {
            if (!helper.connected || !helper.connectedToWifi) {
                connectivityListener.connectionTimeout()
            }

            timeoutHandler!!.removeCallbacks(timeoutRunnable)
        }

        timeoutHandler!!.postDelayed(timeoutRunnable, millis)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (helper.connected) {
            if (helper.connectedToWifi) {
                timeoutHandler?.removeCallbacks(timeoutRunnable)
                connectivityListener.successfulConnected()
            } else {
                helper.disconnect()
            }
        }
    }

}