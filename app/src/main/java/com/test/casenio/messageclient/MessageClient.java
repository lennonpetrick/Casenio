package com.test.casenio.messageclient;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Interface to work with any client of message server
 * */
public interface MessageClient {

    /**
     * Connect to a message server
     *
     * @return A completable
     * */
    Completable connect();

    /**
     * Publish to a topic in the connected server
     *
     * @param topic A topic to publish the message
     * @param message A message to be published
     *
     * @return A completable
     * */
    Completable publish(String topic, String message);

    /**
     * Subscribe to a topic in order to listen to messages
     *
     * @param topic A topic to be listening to messages
     *
     * @return A Observable of {@link String} containing a message
     * */
    Observable<String> subscribe(String topic);

    /**
     * Unsubscribe to a topic for stopping receiving messages
     *
     * @param topic A topic to be unsubscribed
     *
     * @return A completable
     * */
    Completable unsubscribe(String topic);

    /**
     * Disconnect from the connected server
     *
     * @return A completable
     * */
    Completable disconnect();

}
