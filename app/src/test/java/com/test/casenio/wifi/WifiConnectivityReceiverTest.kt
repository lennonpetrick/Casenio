package com.test.casenio.wifi

import android.content.Context
import android.content.Intent
import org.junit.Before
import org.junit.Test

import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class WifiConnectivityReceiverTest {

    @Mock
    private lateinit var helperMock: WifiHelper
    @Mock
    private lateinit var listenerMock: ConnectivityListener
    @Mock
    private lateinit var context: Context
    @Mock
    private lateinit var intent: Intent
    private lateinit var receiver: WifiConnectivityReceiver

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        receiver = WifiConnectivityReceiver(helperMock, listenerMock)
    }

    @Test
    fun onReceive_connectedToWifi() {
        `when`(helperMock.connected).thenReturn(true)
        `when`(helperMock.connectedToWifi).thenReturn(true)

        receiver.onReceive(context, intent)

        verify(listenerMock).successfulConnected()
        verify(helperMock, never()).disconnect()
    }

    @Test
    fun onReceive_connectedToDifferentWifi() {
        `when`(helperMock.connected).thenReturn(true)
        `when`(helperMock.connectedToWifi).thenReturn(false)

        receiver.onReceive(context, intent)

        verify(listenerMock, never()).successfulConnected()
        verify(helperMock).disconnect()
    }

    @Test
    fun onReceive_notConnected() {
        `when`(helperMock.connected).thenReturn(false)
        `when`(helperMock.connectedToWifi).thenReturn(false)

        receiver.onReceive(context, intent)

        verify(listenerMock, never()).successfulConnected()
        verify(helperMock, never()).disconnect()
    }
}