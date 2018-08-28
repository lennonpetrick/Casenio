package com.test.casenio;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View mView;

    public MainPresenter(MainContract.View view) {
        this.mView = view;

        if (!mView.isWifiEnable()) {
            mView.turnWifiOn();
        }
    }
}
