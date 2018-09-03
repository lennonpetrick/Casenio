package com.test.casenio.di

import android.content.Context
import com.test.casenio.MainViewModel
import com.test.casenio.messageclient.MessageClient
import com.test.casenio.messageclient.MqttClient
import com.test.casenio.wifi.WifiHelper
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@Module
class MainActivityModule(private val context: Context) {

    @Provides
    @ApplicationScope
    fun wifiHelper(): WifiHelper {
        return WifiHelper(context)
    }

    @Provides
    @ApplicationScope
    fun compositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

    @Provides
    @ApplicationScope
    fun messageClient(): MessageClient {
        return MqttClient(context)
    }

    @Provides
    @ApplicationScope
    @Inject
    fun viewModel(client: MessageClient): MainViewModel {
        return MainViewModel(client)
    }
}