package com.test.casenio

import com.test.casenio.messageclient.MessageClient
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

private const val TOPIC = "casenio_topic"
private const val MESSAGE = "{\n" +
        "  \"status\": \"OK\",\n" +
        "  \"config\": {\n" +
        "    \"OEM\": \"casenio\"\n" +
        "  }\n" +
        "}"

class MainViewModel(client: MessageClient) {

    val messageObservable: PublishSubject<String> = PublishSubject.create()
    val statusObservable: PublishSubject<Int> = PublishSubject.create()
    private val messageClient = client

    fun connectToClient() {
        connectAndListen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {
                        messageObservable.onSubscribe(d)
                    }

                    override fun onNext(message: String) {
                        messageObservable.onNext(message)
                    }

                    override fun onError(e: Throwable) {
                        messageObservable.onError(e)
                    }

                    override fun onComplete() {
                        messageObservable.onComplete()
                    }
                })
    }

    fun disconnectFromClient() {
        messageClient.unsubscribe(TOPIC)
                .mergeWith(messageClient.disconnect())
                .subscribe(object : CompletableObserver {
                    override fun onSubscribe(d: Disposable) {}

                    override fun onComplete() {}

                    override fun onError(e: Throwable) {}
                })
    }

    private fun connectAndListen(): Observable<String> {
        return messageClient.connect()
                .observeOn(Schedulers.io())
                .andThen(messageClient.publish(TOPIC, MESSAGE))
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.create { e ->
                    statusObservable
                            .onNext(R.string.message_waiting_message)
                    e.onComplete()
                })
                .observeOn(Schedulers.io())
                .andThen(messageClient.subscribe(TOPIC))
    }

}