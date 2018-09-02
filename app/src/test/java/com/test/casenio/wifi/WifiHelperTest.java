package com.test.casenio.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class WifiHelperTest {

    @Mock
    private Context mContextMock;

    @Mock
    private WifiManager mWifiManager;

    private WifiHelper mHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(mContextMock.getApplicationContext()).thenReturn(mContextMock);
        when(mContextMock.getSystemService(Context.WIFI_SERVICE)).thenReturn(mWifiManager);

        mHelper = WifiHelper.getInstance(mContextMock);
    }

    @After
    public void tearDown() {
        WifiHelper.destroy();
    }

    @Test
    public void isConnected() {
        ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        NetworkInfo activeNetwork = Mockito.mock(NetworkInfo.class);

        when(mContextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);
        when(manager.getActiveNetworkInfo()).thenReturn(activeNetwork);
        when(activeNetwork.isConnected()).thenReturn(true);

        assertTrue(mHelper.isConnected());
    }

    @Test
    public void isNotConnected() {
        ConnectivityManager manager = Mockito.mock(ConnectivityManager.class);
        NetworkInfo activeNetwork = Mockito.mock(NetworkInfo.class);

        when(mContextMock.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(manager);
        when(manager.getActiveNetworkInfo()).thenReturn(activeNetwork);
        when(activeNetwork.isConnected()).thenReturn(false);

        assertFalse(mHelper.isConnected());
    }
}