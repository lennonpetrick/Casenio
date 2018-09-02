package com.test.casenio;

public interface MainContract {

    interface View {
        boolean isWifiEnable();
        void turnWifiOn();
        void showMessage(String message);
        void displayResult(String result);
    }

    interface Presenter {
        void connectToClient();
        void disconnectFromClient();
    }
}
