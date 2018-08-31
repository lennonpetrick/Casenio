package com.test.casenio;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainPresenter implements MainContract.Presenter {

    private static final String TOPIC = "casenio_topic";
    private static final String SERVER_URI = "tcp://m20.cloudmqtt.com:15662";
    private static final String USER = "rjnpkmdw";
    private static final String PASSWORD = "WfqwIoROiN5S";

    private MainContract.View mView;
    private MqttAndroidClient mClient;

    public MainPresenter(MainContract.View view) {
        this.mView = view;

        if (!mView.isWifiEnable()) {
            mView.turnWifiOn();
        }
    }

    @Override
    public void connect(Context context) {
        final String clientId = MqttClient.generateClientId();
        mClient = new MqttAndroidClient(context.getApplicationContext(), SERVER_URI, clientId);

        try {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(false);
            options.setUserName(USER);
            options.setPassword(PASSWORD.toCharArray());

            mClient.connect(null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    subscribeToTopic(TOPIC);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    exception.printStackTrace();
                    mView.showMessage(exception.getMessage());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            mView.showMessage(e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        unsubscribeFromTopic(TOPIC);
        disconnectFromClient();
    }

    private void subscribeToTopic(String topic) {
        try {
            mClient.subscribe(topic, 1, (comingTopic, message) -> {
                mView.showMessage(message.toString());
            });
        } catch (MqttException e) {
            e.printStackTrace();
            mView.showMessage(e.getMessage());
        }
    }

    private void unsubscribeFromTopic(String topic) {
        try {
            mClient.unsubscribe(topic);
        } catch (MqttException e) {
            e.printStackTrace();
            mView.showMessage(e.getMessage());
        }
    }

    private void disconnectFromClient() {
        try {
            mClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
            mView.showMessage(e.getMessage());
        }
    }
}
