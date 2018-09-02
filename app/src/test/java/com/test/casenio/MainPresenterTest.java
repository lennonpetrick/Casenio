package com.test.casenio;

import com.test.casenio.messageclient.MessageClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainPresenterTest {

    @Mock
    private MainContract.View mView;

    @Mock
    private MessageClient mMessageClient;

    private MainContract.Presenter mPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setUpSchedulers();
        mPresenter = new MainPresenter(mView, mMessageClient, new CompositeDisposable());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void connectToClient_success() {
        final String message = "test";
        Completable completable = Completable.complete();
        when(mMessageClient.connect()).thenReturn(completable);
        when(mMessageClient.publish(anyString(), anyString())).thenReturn(completable);
        when(mMessageClient.subscribe(anyString())).thenReturn(Observable.just(message));

        mPresenter.connectToClient();

        verify(mMessageClient).connect();
        verify(mMessageClient).publish(anyString(), anyString());
        verify(mMessageClient).subscribe(anyString());
        verify(mView).setConnectionStatus(R.string.message_connecting_client);
        verify(mView).setConnectionStatus(R.string.message_waiting_message);
        verify(mView).displayResult(message);
        verify(mView).hideConnectionStatus();
        verify(mView, never()).showMessage(any());
    }

    @Test
    public void connectToClient_error() {
        final String message = "test";
        Completable completable = Completable.complete();
        when(mMessageClient.connect()).thenReturn(completable);
        when(mMessageClient.publish(anyString(), anyString()))
                .thenReturn(Completable.error(new Throwable()));
        when(mMessageClient.subscribe(anyString())).thenReturn(Observable.just(message));

        mPresenter.connectToClient();

        verify(mMessageClient).connect();
        verify(mMessageClient).publish(anyString(), anyString());
        verify(mView).setConnectionStatus(R.string.message_connecting_client);
        verify(mView).hideConnectionStatus();
        verify(mView).showMessage(any());
        verify(mView, never()).setConnectionStatus(R.string.message_waiting_message);
        verify(mView, never()).displayResult(message);
    }

    @Test
    public void disconnectFromClient() {
        Completable completable = Completable.complete();
        when(mMessageClient.unsubscribe(anyString())).thenReturn(completable);
        when(mMessageClient.disconnect()).thenReturn(completable);

        mPresenter.disconnectFromClient();

        verify(mMessageClient).unsubscribe(anyString());
        verify(mMessageClient).disconnect();
    }

    private void setUpSchedulers() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }
}