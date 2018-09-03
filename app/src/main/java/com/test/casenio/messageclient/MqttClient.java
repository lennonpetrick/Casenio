package com.test.casenio.messageclient;

import android.content.Context;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import io.reactivex.Completable;
import io.reactivex.Observable;

public class MqttClient implements MessageClient {

    private static final String SERVER_URI = "tcp://m20.cloudmqtt.com:15662";
    private static final String USER = "kekiwtyo";
    private static final String PASSWORD = "Az_Lk_XM_oBN";

    private MqttAndroidClient mMqttClient;

    public MqttClient(Context context) {
        mMqttClient = new MqttAndroidClient(context,
                SERVER_URI, org.eclipse.paho.client.mqttv3.MqttClient.generateClientId());
    }

    @Override
    public Completable connect() {
        return Completable.create(emitter -> {
            try {
                MqttConnectOptions options = new MqttConnectOptions();
                options.setCleanSession(true);
                options.setUserName(USER);
                options.setPassword(PASSWORD.toCharArray());

                mMqttClient.connect(options, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        emitter.tryOnError(exception);
                    }
                });
            } catch (MqttException exception) {
                emitter.tryOnError(exception);
            }
        });
    }

    @Override
    public Completable publish(final String topic, final String message) {
        return Completable.create(emitter -> {
            try {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mMqttClient.publish(topic, mqttMessage,
                        null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        emitter.onComplete();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        emitter.tryOnError(exception);
                    }
                });
            } catch (MqttException exception) {
                emitter.tryOnError(exception);
            }
        });
    }

    @Override
    public Observable<String> subscribe(final String topic) {
        return Observable.create(emitter -> {
            try {
                mMqttClient.subscribe(topic, 1,
                        (comingTopic, message) -> emitter.onNext(message.toString()));
            } catch (MqttException exception) {
                emitter.tryOnError(exception);
            }
        });
    }

    @Override
    public Completable unsubscribe(final String topic) {
        return Completable.create(emitter -> {
            try {
                mMqttClient.unsubscribe(topic);
                emitter.onComplete();
            } catch (Exception exception) {
                emitter.tryOnError(exception);
            }
        });
    }

    @Override
    public Completable disconnect() {
        return Completable.create(emitter -> {
            try {
                mMqttClient.disconnect();
                mMqttClient.unregisterResources();
                mMqttClient.close();
                emitter.onComplete();
            } catch (Exception exception) {
                emitter.tryOnError(exception);
            }
        });
    }
}
