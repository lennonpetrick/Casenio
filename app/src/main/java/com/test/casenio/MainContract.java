package com.test.casenio;

public interface MainContract {

    interface View {
        boolean isWifiEnable();
        void turnWifiOn();
    }

    interface Presenter {

        void connect(String ssid, String password);

    }
}
