package com.test.casenio.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class WifiHelperTest {

    @Mock
    private lateinit var contextMock: Context
    @Mock
    private lateinit var wifiManager: WifiManager
    private lateinit var helper: WifiHelper

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        `when`<Context>(contextMock.applicationContext).thenReturn(contextMock)
        `when`<Any>(contextMock.getSystemService(Context.WIFI_SERVICE)).thenReturn(wifiManager)

        helper = WifiHelper(contextMock)
    }

    @Test
    fun isConnected() {
        val manager = Mockito.mock(ConnectivityManager::class.java)
        val activeNetwork = Mockito.mock(NetworkInfo::class.java)

        `when`(contextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager)
        `when`(manager.activeNetworkInfo).thenReturn(activeNetwork)
        `when`(activeNetwork.isConnected).thenReturn(true)

        assertTrue(helper.connected)
    }

    @Test
    fun isNotConnected() {
        val manager = Mockito.mock(ConnectivityManager::class.java)
        val activeNetwork = Mockito.mock(NetworkInfo::class.java)

        `when`(contextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager)
        `when`(manager.activeNetworkInfo).thenReturn(activeNetwork)
        `when`(activeNetwork.isConnected).thenReturn(false)

        assertFalse(helper.connected)
    }

}