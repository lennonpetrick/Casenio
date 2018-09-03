package com.test.casenio.wifi

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import java.lang.ref.WeakReference

class WifiHelper(context: Context) {

    private val wifiManager = context.applicationContext
            .getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var weakContext = WeakReference(context)
    private var receiver: WifiConnectivityReceiver? = null
    private var ssid: String? = null

    var wifiEnable: Boolean
        get() = wifiManager.isWifiEnabled
        set(param) {
            wifiManager.isWifiEnabled = param
        }

    val connected: Boolean
        get() {
            val manager = weakContext.get()?.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val info = manager.activeNetworkInfo
            return info != null && info.isConnected
        }

    val connectedToWifi: Boolean
        get() {
            return wifiManager.connectionInfo.ssid == ssid
        }

    fun connectToWifi(localSsid: String, password: String, timeout: Long,
                      listener: ConnectivityListener) {
        val config = createConfiguration(localSsid, password)
        val networkId = wifiManager.addNetwork(config)
        wifiManager.disconnect()
        wifiManager.enableNetwork(networkId, true)
        wifiManager.reconnect()

        registerReceiver(timeout, listener)
    }

    private fun createConfiguration(localSsid: String, password: String): WifiConfiguration {
        val config = WifiConfiguration()
        ssid = String.format("\"%s\"", localSsid)
        config.SSID = ssid
        config.preSharedKey = String.format("\"%s\"", password)
        config.hiddenSSID = true
        return config
    }

    private fun registerReceiver(timeout: Long, listener: ConnectivityListener) {
        receiver = WifiConnectivityReceiver(this,
                object : ConnectivityListener {
                    override fun successfulConnected() {
                        unregisterReceiver()
                        listener.successfulConnected()
                    }

                    override fun connectionTimeout() {
                        unregisterReceiver()
                        disconnect()
                        listener.connectionTimeout()
                    }
                })

        receiver!!.timeout(timeout)
        weakContext.get()?.registerReceiver(receiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    private fun unregisterReceiver() {
        weakContext.get()?.unregisterReceiver(receiver!!)
        receiver = null
    }

    fun disconnect() {
        wifiManager.disconnect()
    }
}