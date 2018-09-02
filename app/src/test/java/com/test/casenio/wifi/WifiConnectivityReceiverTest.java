package com.test.casenio.wifi;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WifiConnectivityReceiverTest {

    @Mock
    private WifiHelper mHelperMock;

    @Mock
    private ConnectivityListener mListenerMock;

    private WifiConnectivityReceiver mReceiver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mReceiver = new WifiConnectivityReceiver(mHelperMock, mListenerMock);
    }

    @Test
    public void onReceive_connectedToWifi() {
        when(mHelperMock.isConnected()).thenReturn(true);
        when(mHelperMock.isConnectedToWifi()).thenReturn(true);

        mReceiver.onReceive(null, null);

        verify(mListenerMock).successfulConnected();
        verify(mHelperMock, never()).disconnect();
    }

    @Test
    public void onReceive_connectedToDifferentWifi() {
        when(mHelperMock.isConnected()).thenReturn(true);
        when(mHelperMock.isConnectedToWifi()).thenReturn(false);

        mReceiver.onReceive(null, null);

        verify(mListenerMock, never()).successfulConnected();
        verify(mHelperMock).disconnect();
    }

    @Test
    public void onReceive_notConnected() {
        when(mHelperMock.isConnected()).thenReturn(false);
        when(mHelperMock.isConnectedToWifi()).thenReturn(false);

        mReceiver.onReceive(null, null);

        verify(mListenerMock, never()).successfulConnected();
        verify(mHelperMock, never()).disconnect();
    }
}