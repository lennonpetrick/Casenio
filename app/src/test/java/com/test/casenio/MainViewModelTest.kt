package com.test.casenio

import com.test.casenio.messageclient.MessageClient
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class MainViewModelTest {

    @Mock
    private lateinit var messageClient: MessageClient
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        setUpSchedulers()
        viewModel = MainViewModel(messageClient)
    }

    @After
    fun tearDown() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }

    @Test
    fun connectToClient() {
        val completable = Completable.complete()
        `when`<Completable>(messageClient.connect()).thenReturn(completable)
        `when`<Completable>(messageClient.publish(anyString(), anyString()))
                .thenReturn(completable)
        `when`<Observable<String>>(messageClient.subscribe(anyString()))
                .thenReturn(Observable.just(""))

        viewModel.connectToClient()

        verify<MessageClient>(messageClient).connect()
        verify<MessageClient>(messageClient).publish(anyString(), anyString())
        verify<MessageClient>(messageClient).subscribe(anyString())
    }

    @Test
    fun disconnectFromClient() {
        val completable = Completable.complete()
        `when`(messageClient.unsubscribe(anyString())).thenReturn(completable)
        `when`(messageClient.disconnect()).thenReturn(completable)

        viewModel.disconnectFromClient()

        verify(messageClient).unsubscribe(anyString())
        verify(messageClient).disconnect()
    }

    private fun setUpSchedulers() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }
}