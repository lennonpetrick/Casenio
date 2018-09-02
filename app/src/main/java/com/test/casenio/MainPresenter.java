package com.test.casenio;

import com.test.casenio.messageclient.MessageClient;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private static final String TOPIC = "casenio_topic";
    private static final String MESSAGE = "{\n" +
                                            "  \"status\": \"OK\",\n" +
                                            "  \"config\": {\n" +
                                            "    \"OEM\": \"casenio\"\n" +
                                            "  }\n" +
                                            "}";

    private MainContract.View mView;
    private MessageClient mMessageClient;
    private CompositeDisposable mDisposable;

    public MainPresenter(MainContract.View view, MessageClient client,
                         CompositeDisposable disposable) {
        this.mView = view;
        this.mMessageClient = client;
        this.mDisposable = disposable;

        if (!mView.isWifiEnable()) {
            mView.turnWifiOn();
        }
    }

    @Override
    public void connectToClient() {
        mView.setConnectionStatus(R.string.message_connecting_client);
        mDisposable.add(connectAndListen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(message -> {
                    mView.displayResult(message);
                    mView.hideConnectionStatus();
                },
                throwable -> {
                    throwable.printStackTrace();
                    mView.showMessage(throwable.getMessage());
                }));
    }

    @Override
    public void disconnectFromClient() {
        mDisposable.add(mMessageClient.unsubscribe(TOPIC)
                .mergeWith(mMessageClient.disconnect())
                .subscribe(() -> {}, Throwable::printStackTrace));
    }

    private Observable<String> connectAndListen() {
        return mMessageClient.connect()
                .observeOn(Schedulers.io())
                .andThen(mMessageClient.publish(TOPIC, MESSAGE))
                .observeOn(AndroidSchedulers.mainThread())
                .andThen(Completable.create(e -> mView
                        .setConnectionStatus(R.string.message_waiting_message)))
                .observeOn(Schedulers.io())
                .andThen(mMessageClient.subscribe(TOPIC));
    }
}
