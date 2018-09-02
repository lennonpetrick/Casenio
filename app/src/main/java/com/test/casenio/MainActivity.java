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
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.container_fields) View mContainerFields;
    @BindView(R.id.container_result) View mContainerResult;
    @BindView(R.id.edSSID) EditText mEdSSID;
    @BindView(R.id.edPassword) EditText mEdPassword;
    @BindView(R.id.tvResult) TextView mTvResult;
    @BindView(R.id.tvConnectionStatus) TextView mTvConnectionStatus;

    @Inject WifiHelper mWifiHelper;
    @Inject CompositeDisposable mDisposable;
    @Inject MainViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        injectDependencies();
        setListeners();
        subscribeToMessagePublisher();
        subscribeToStatusPublisher();

        if (!isWifiEnable()) {
            turnWifiOn();
        }
    }

    @Override
    protected void onDestroy() {
        mViewModel.disconnectFromClient();
        mViewModel = null;

        WifiHelper.destroy();
        mWifiHelper = null;

        mDisposable.clear();
        mDisposable = null;
        super.onDestroy();
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
                        connectToClient();
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
            connectToClient();
        } else {
            connect();
        }
    }

    private void setListeners() {
        mDisposable.add(RxTextView.editorActions(mEdPassword)
                .filter(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(integer -> connect()));
    }

    private void connectToClient() {
        setConnectionStatus(R.string.message_connecting_client);
        mViewModel.connectToClient();
    }

    private void subscribeToMessagePublisher() {
        mViewModel.getMessageObservable()
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(String message) {
                        displayResult(message);
                        hideConnectionStatus();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        hideConnectionStatus();
                        showMessage(throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                    }
                });

    }

    private void subscribeToStatusPublisher() {
        mViewModel.getStatusObservable()
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable.add(d);
                    }

                    @Override
                    public void onNext(Integer messageResId) {
                        setConnectionStatus(messageResId);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        hideConnectionStatus();
                        showMessage(throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {}
                });
    }

    private boolean isWifiEnable() {
        return mWifiHelper.isWifiEnable();
    }

    private void turnWifiOn() {
        mWifiHelper.setWifiEnable(true);
    }

    private void showMessage(String message) {
        Window window = getWindow();
        View view = window.getCurrentFocus();
        if (view == null) {
            view = window.getDecorView();
        }

        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_main_activity_snack_bar_retry, v -> retry())
                .show();
    }

    private void displayResult(String result) {
        mContainerFields.setVisibility(View.GONE);
        mTvResult.setText(result);
        mContainerResult.setVisibility(View.VISIBLE);
    }

    private void setConnectionStatus(int resId) {
        mTvConnectionStatus.setText(resId);
        mTvConnectionStatus.setVisibility(View.VISIBLE);
    }

    private void hideConnectionStatus() {
        mTvConnectionStatus.setVisibility(View.GONE);
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
