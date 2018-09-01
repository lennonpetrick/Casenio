package com.test.casenio;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.test.casenio.wifi.ConnectivityListener;
import com.test.casenio.wifi.WifiHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.container_fields) View mContainerFields;
    @BindView(R.id.edSSID) EditText mEdSSID;
    @BindView(R.id.edPassword) EditText mEdPassword;

    private MainContract.Presenter mPresenter;
    private WifiHelper mWifiHelper;
    private CompositeDisposable mListenersDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mWifiHelper = WifiHelper.getInstance(this);
        mListenersDisposable = new CompositeDisposable();
        mPresenter = new MainPresenter(this);

        setListeners();
    }

    @Override
    protected void onDestroy() {
        WifiHelper.destroy();
        mWifiHelper = null;
        mListenersDisposable.clear();
        mListenersDisposable = null;
        mPresenter.disconnect();
        mPresenter = null;
        super.onDestroy();
    }

    @Override
    public boolean isWifiEnable() {
        return mWifiHelper.isWifiEnable();
    }

    @Override
    public void turnWifiOn() {
        mWifiHelper.setWifiEnable(true);
    }

    @Override
    public void showMessage(String message) {
        Window window = getWindow();
        View view = window.getCurrentFocus();
        if (view == null) {
            view = window.getDecorView();
        }

        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_main_activity_snack_bar_retry, v -> retry())
                .show();
    }

    @OnClick(R.id.btnConnect)
    void connect() {
        dismissKeyboard();

        final String ssid = mEdSSID.getText().toString();
        final String password = mEdPassword.getText().toString();
        mWifiHelper.connectToWifi(ssid, password, 180000, // 3 Minutes
                new ConnectivityListener() {
                    @Override
                    public void successfulConnected() {
                        mPresenter.connect(MainActivity.this);
                    }

                    @Override
                    public void connectionTimeout() {
                        showMessage(getString(R.string.message_connection_timeout));
                    }
                });
    }

    private void retry() {
        if (mWifiHelper.isConnected()
                && mWifiHelper.isConnectedToWifi()) {
            mPresenter.connect(this);
        } else {
            connect();
        }
    }

    private void setListeners() {
        mListenersDisposable.add(RxTextView.editorActions(mEdPassword)
                .filter(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(integer -> connect()));
    }


    private void dismissKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        View view = getCurrentFocus();
        if (inputManager != null && view != null) {
            view.clearFocus();
            inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
    }

}
