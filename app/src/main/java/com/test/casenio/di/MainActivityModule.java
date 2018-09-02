package com.test.casenio.di;

import android.content.Context;

import com.test.casenio.MainContract;
import com.test.casenio.MainPresenter;
import com.test.casenio.messageclient.MessageClient;
import com.test.casenio.messageclient.MqttClient;
import com.test.casenio.wifi.WifiHelper;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import io.reactivex.disposables.CompositeDisposable;

@Module
public class MainActivityModule {

    private Context mContext;

    public MainActivityModule(Context context) {
        this.mContext = context;
    }

    @Provides
    @ApplicationScope
    WifiHelper wifiHelper() {
        return WifiHelper.getInstance(mContext);
    }

    @Provides
    @ApplicationScope
    CompositeDisposable compositeDisposable() {
        return new CompositeDisposable();
    }

    @Provides
    @ApplicationScope
    MessageClient messageClient() {
        return new MqttClient(mContext);
    }

    @Provides
    @ApplicationScope
    MainContract.View mainView() {
        return (MainContract.View) mContext;
    }

    @Provides
    @ApplicationScope
    @Inject
    MainContract.Presenter mainPresenter(MainContract.View view, MessageClient client,
                                    CompositeDisposable disposable) {
        return new MainPresenter(view, client, disposable);
    }
}
