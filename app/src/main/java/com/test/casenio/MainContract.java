package com.test.casenio;

import android.content.Context;

public interface MainContract {

    interface View {
        boolean isWifiEnable();
        void turnWifiOn();
        void showMessage(String message);
    }

    interface Presenter {
        void connect(Context context);
        void disconnect();
    }
}
