package com.test.casenio;

import com.test.casenio.old.MainViewModel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MainViewModelTest {

    @Mock
    private MessageClient mMessageClient;

    private MainViewModel mViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setUpSchedulers();
        mViewModel = new MainViewModel(mMessageClient);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void connectToClient() {
        Completable completable = Completable.complete();
        when(mMessageClient.connect()).thenReturn(completable);
        when(mMessageClient.publish(anyString(), anyString())).thenReturn(completable);
        when(mMessageClient.subscribe(anyString())).thenReturn(Observable.just(""));

        mViewModel.connectToClient();

        verify(mMessageClient).connect();
        verify(mMessageClient).publish(anyString(), anyString());
        verify(mMessageClient).subscribe(anyString());
    }

    @Test
    public void disconnectFromClient() {
        Completable completable = Completable.complete();
        when(mMessageClient.unsubscribe(anyString())).thenReturn(completable);
        when(mMessageClient.disconnect()).thenReturn(completable);

        mViewModel.disconnectFromClient();

        verify(mMessageClient).unsubscribe(anyString());
        verify(mMessageClient).disconnect();
    }

    private void setUpSchedulers() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

}