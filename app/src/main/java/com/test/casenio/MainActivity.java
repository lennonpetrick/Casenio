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
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.test.casenio.di.DaggerMainComponent;
import com.test.casenio.di.MainActivityModule;
import com.test.casenio.wifi.ConnectivityListener;
import com.test.casenio.wifi.WifiHelper;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    @BindView(R.id.container_fields) View mContainerFields;
    @BindView(R.id.container_result) View mContainerResult;
    @BindView(R.id.edSSID) EditText mEdSSID;
    @BindView(R.id.edPassword) EditText mEdPassword;
    @BindView(R.id.tvResult) TextView mTvResult;
    @BindView(R.id.tvConnectionStatus) TextView mTvConnectionStatus;

    @Inject WifiHelper mWifiHelper;
    @Inject CompositeDisposable mDisposable;
    @Inject MainContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        injectDependencies();
        setListeners();
    }

    @Override
    protected void onDestroy() {
        mPresenter.disconnectFromClient();
        mPresenter = null;

        WifiHelper.destroy();
        mWifiHelper = null;

        mDisposable.clear();
        mDisposable = null;
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

    @Override
    public void displayResult(String result) {
        mContainerFields.setVisibility(View.GONE);
        mTvResult.setText(result);
        mContainerResult.setVisibility(View.VISIBLE);
    }

    @Override
    public void setConnectionStatus(int resId) {
        mTvConnectionStatus.setText(resId);
        mTvConnectionStatus.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideConnectionStatus() {
        mTvConnectionStatus.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnConnect)
    void connect() {
        dismissKeyboard();
        setConnectionStatus(R.string.message_connecting);

        final String ssid = mEdSSID.getText().toString();
        final String password = mEdPassword.getText().toString();
        mWifiHelper.connectToWifi(ssid, password, 180000, // 3 Minutes
                new ConnectivityListener() {
                    @Override
                    public void successfulConnected() {
                        mPresenter.connectToClient();
                    }

                    @Override
                    public void connectionTimeout() {
                        hideConnectionStatus();
                        showMessage(getString(R.string.message_connection_timeout));
                    }
                });
    }

    private void injectDependencies() {
        DaggerMainComponent
                .builder()
                .mainActivityModule(new MainActivityModule(this))
                .build()
                .inject(this);
    }

    private void retry() {
        if (mWifiHelper.isConnected()
                && mWifiHelper.isConnectedToWifi()) {
            mPresenter.connectToClient();
        } else {
            connect();
        }
    }

    private void setListeners() {
        mDisposable.add(RxTextView.editorActions(mEdPassword)
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
