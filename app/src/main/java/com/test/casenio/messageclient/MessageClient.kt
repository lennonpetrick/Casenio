package com.test.casenio.messageclient

import io.reactivex.Completable
import io.reactivex.Observable

/**
 * Interface to work with any client of message server
 */
interface MessageClient {

    /**
     * Connect to a message server
     *
     * @return A completable
     */
    fun connect(): Completable

    /**
     * Publish to a topic in the connected server
     *
     * @param topic A topic to publish the message
     * @param message A message to be published
     *
     * @return A completable
     */
    fun publish(topic: String, message: String): Completable

    /**
     * Subscribe to a topic in order to listen to messages
     *
     * @param topic A topic to be listening to messages
     *
     * @return A Observable of [String] containing a message
     */
    fun subscribe(topic: String): Observable<String>

    /**
     * Unsubscribe to a topic for stopping receiving messages
     *
     * @param topic A topic to be unsubscribed
     *
     * @return A completable
     */
    fun unsubscribe(topic: String): Completable

    /**
     * Disconnect from the connected server
     *
     * @return A completable
     */
    fun disconnect(): Completable

}
