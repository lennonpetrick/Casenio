package com.test.casenio.wifi

interface ConnectivityListener {
    fun successfulConnected()
    fun connectionTimeout()
}