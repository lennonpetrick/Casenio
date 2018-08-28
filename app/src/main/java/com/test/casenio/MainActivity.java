package com.test.casenio;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private MainContract.Presenter mPresenter;
    private WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        mPresenter = new MainPresenter(this);
    }

    @Override
    public boolean isWifiEnable() {
        return mWifiManager.isWifiEnabled();
    }

    @Override
    public void turnWifiOn() {
        mWifiManager.setWifiEnabled(true);
    }


}
