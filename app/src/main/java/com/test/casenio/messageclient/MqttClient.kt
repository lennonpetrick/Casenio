package com.test.casenio.messageclient

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Observable
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttClient(context: Context) : MessageClient {

    private val SERVER_URI = "tcp://m20.cloudmqtt.com:15662"
    private val mqttClient = MqttAndroidClient(context, SERVER_URI,
            org.eclipse.paho.client.mqttv3.MqttClient.generateClientId())

    override fun connect(): Completable {
        return Completable.create { emitter ->
            try {
                mqttClient.connect(null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        emitter.onComplete()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        emitter.tryOnError(exception)
                    }
                })
            } catch (exception: MqttException) {
                emitter.tryOnError(exception)
            }
        }
    }

    override fun publish(topic: String, message: String): Completable {
        return Completable.create { emitter ->
            try {
                val mqttMessage = MqttMessage(message.toByteArray())
                mqttClient.publish(topic, mqttMessage,
                        null, object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken) {
                        emitter.onComplete()
                    }

                    override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                        emitter.tryOnError(exception)
                    }
                })
            } catch (exception: MqttException) {
                emitter.tryOnError(exception)
            }
        }
    }

    override fun subscribe(topic: String): Observable<String> {
        return Observable.create { emitter ->
            try {
                mqttClient.subscribe(topic, 1
                ) { _, message -> emitter.onNext(message.toString()) }
            } catch (exception: MqttException) {
                emitter.tryOnError(exception)
            }
        }
    }

    override fun unsubscribe(topic: String): Completable {
        return Completable.create { emitter ->
            try {
                mqttClient.unsubscribe(topic)
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.tryOnError(exception)
            }
        }
    }

    override fun disconnect(): Completable {
        return Completable.create { emitter ->
            try {
                mqttClient.disconnect()
                mqttClient.unregisterResources()
                mqttClient.close()
                emitter.onComplete()
            } catch (exception: Exception) {
                emitter.tryOnError(exception)
            }
        }
    }
}