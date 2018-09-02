package com.test.casenio;

import com.test.casenio.messageclient.MessageClient;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class MainViewModel {

    private static final String TOPIC = "casenio_topic";
    private static final String MESSAGE = "{\n" +
            "  \"status\": \"OK\",\n" +
            "  \"config\": {\n" +
            "    \"OEM\": \"casenio\"\n" +
            "  }\n" +
            "}";

    private MessageClient mMessageClient;
    private PublishSubject<String> mMessageObservable;
    private PublishSubject<Integer> mStatusObservable;

    public MainViewModel(MessageClient client) {
        this.mMessageClient = client;
        this.mMessageObservable = PublishSubject.create();
        this.mStatusObservable = PublishSubject.create();
    }

    public PublishSubject<String> getMessageObservable() {
        return mMessageObservable;
    }

    public PublishSubject<Integer> getStatusObservable() {
        return mStatusObservable;
    }

    public void connectToClient() {
        connectAndListen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mMessageObservable.onSubscribe(d);
                    }

                    @Override
                    public void onNext(String message) {
                        mMessageObservable.onNext(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mMessageObservable.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        mMessageObservable.onComplete();
                    }
                });
    }

    public void disconnectFromClient() {
        mMessageClient.unsubscribe(TOPIC)
                .mergeWith(mMessageClient.disconnect())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {}

                    @Override
                    public void onComplete() {}

                    @Override
                    public void onError(Throwable e) {}
                });
    }

    private Observable<String> connectAndListen() {
        return mMessageClient.connect()
                .observeOn(Schedulers.io())
                .andThen(mMessageClient.publish(TOPIC, MESSAGE))
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.create(e -> mStatusObservable
                        .onNext(R.string.message_waiting_message)))
                .observeOn(Schedulers.io())
                .andThen(mMessageClient.subscribe(TOPIC));
    }

}
