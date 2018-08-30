package com.test.casenio;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.container_fields) View mContainerFields;
    @BindView(R.id.edSSID) EditText mEdSSID;
    @BindView(R.id.edPassword) EditText mEdPassword;

    private MainContract.Presenter mPresenter;
    private WifiManager mWifiManager;
    private CompositeDisposable mListenersDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mWifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        mListenersDisposable = new CompositeDisposable();
        mPresenter = new MainPresenter(this);

        setListeners();
    }

    @Override
    protected void onDestroy() {
        mListenersDisposable.clear();
        mListenersDisposable = null;
        super.onDestroy();
    }

    @Override
    public boolean isWifiEnable() {
        return mWifiManager.isWifiEnabled();
    }

    @Override
    public void turnWifiOn() {
        mWifiManager.setWifiEnabled(true);
    }

    @OnClick(R.id.btnConnect)
    void connect() {
        connect(mEdSSID.getText().toString(),
                mEdPassword.getText().toString());
    }

    private void setListeners() {
        mListenersDisposable.add(RxTextView.editorActions(mEdPassword)
                .filter(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(integer -> connect()));
    }

    private void connect(String ssid, String password) {
        WifiConfiguration config = createConfiguration(ssid, password);
        mWifiManager.addNetwork(config);
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(config.networkId, true);
        mWifiManager.reconnect();
    }

    private WifiConfiguration createConfiguration(String ssid, String password) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = String.format("\"%s\"", ssid);
        config.preSharedKey = String.format("\"%s\"", password);
        return config;
    }
}
