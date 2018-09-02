package com.test.casenio;

import android.support.annotation.StringRes;

public interface MainContract {

    interface View {
        boolean isWifiEnable();
        void turnWifiOn();
        void showMessage(String message);
        void displayResult(String result);
        void setConnectionStatus(@StringRes int resId);
        void hideConnectionStatus();
    }

    interface Presenter {
        void connectToClient();
        void disconnectFromClient();
    }
}
