package com.test.casenio

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.jakewharton.rxbinding2.widget.RxTextView
import com.test.casenio.di.DaggerMainComponent
import com.test.casenio.di.MainActivityModule
import com.test.casenio.wifi.ConnectivityListener
import com.test.casenio.wifi.WifiHelper
import io.reactivex.Observer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @BindView(R.id.container_fields) lateinit var containerFields: View
    @BindView(R.id.container_result) lateinit var containerResult: View
    @BindView(R.id.edSSID) lateinit var edSSID: EditText
    @BindView(R.id.edPassword) lateinit var edPassword: EditText
    @BindView(R.id.tvResult) lateinit var tvResult: TextView
    @BindView(R.id.tvConnectionStatus) lateinit var tvConnectionStatus: TextView

    @Inject lateinit var wifiHelper: WifiHelper
    @Inject lateinit var disposable: CompositeDisposable
    @Inject lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        injectDependencies()
        setListeners()
        subscribeToMessagePublisher()
        subscribeToStatusPublisher()

        if (!isWifiEnable())
            turnWifiOn()
    }

    override fun onDestroy() {
        viewModel.disconnectFromClient()
        disposable.clear()
        super.onDestroy()
    }

    @OnClick(R.id.btnConnect) fun connect() {
        dismissKeyboard()
        setConnectionStatus(R.string.message_connecting)

        val ssid = edSSID.text.toString()
        val password = edPassword.text.toString()

        wifiHelper.connectToWifi(ssid, password, 180000, // 3 Minutes
                object : ConnectivityListener {
                    override fun successfulConnected() {
                        connectToClient()
                    }

                    override fun connectionTimeout() {
                        hideConnectionStatus()
                        showMessage(getString(R.string.message_connection_timeout))
                    }
                })
    }

    private fun injectDependencies() {
        DaggerMainComponent
                .builder()
                .mainActivityModule(MainActivityModule(this))
                .build()
                .inject(this)
    }

    private fun retry() {
        if (wifiHelper.connected && wifiHelper.connectedToWifi) {
            connectToClient()
        } else {
            connect()
        }
    }

    private fun setListeners() {
        disposable.add(RxTextView.editorActions(edPassword)
                .filter { actionId -> actionId == EditorInfo.IME_ACTION_DONE }
                .subscribe {connect()})
    }

    private fun connectToClient() {
        setConnectionStatus(R.string.message_connecting_client)
        viewModel.connectToClient()
    }

    private fun subscribeToMessagePublisher() {
        viewModel.messageObservable
                .subscribe(object : Observer<String> {
                    override fun onSubscribe(d: Disposable) {
                        disposable.add(d)
                    }

                    override fun onNext(message: String) {
                        displayResult(message)
                        hideConnectionStatus()
                    }

                    override fun onError(throwable: Throwable) {
                        hideConnectionStatus()
                        showMessage(throwable.message!!)
                    }

                    override fun onComplete() {}
                })

    }

    private fun subscribeToStatusPublisher() {
        viewModel.statusObservable
                .subscribe(object : Observer<Int> {
                    override fun onSubscribe(d: Disposable) {
                        disposable.add(d)
                    }

                    override fun onNext(messageResId: Int) {
                        setConnectionStatus(messageResId)
                    }

                    override fun onError(throwable: Throwable) {
                        hideConnectionStatus()
                        showMessage(throwable.message!!)
                    }

                    override fun onComplete() {}
                })
    }

    private fun isWifiEnable(): Boolean {
        return wifiHelper.wifiEnable
    }

    private fun turnWifiOn() {
        wifiHelper.wifiEnable = true
    }

    private fun showMessage(message: String) {
        var view = window.currentFocus
        if (view == null) {
            view = window.decorView
        }

        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG)
                .setAction(R.string.btn_main_activity_snack_bar_retry) {retry()}
                .show()
    }

    private fun displayResult(result: String) {
        containerFields.visibility = View.GONE
        tvResult.text = result
        containerResult.visibility = View.VISIBLE
    }

    private fun setConnectionStatus(resId: Int) {
        tvConnectionStatus.setText(resId)
        tvConnectionStatus.visibility = View.VISIBLE
    }

    private fun hideConnectionStatus() {
        tvConnectionStatus.visibility = View.GONE
    }

    private fun dismissKeyboard() {
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        currentFocus?.clearFocus()
        inputManager.hideSoftInputFromWindow(currentFocus?.windowToken,
                InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }
}
