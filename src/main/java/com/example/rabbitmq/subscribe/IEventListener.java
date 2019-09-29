package com.example.rabbitmq.subscribe;

import com.example.rabbitmq.domain.Event;

/**
 * The interface Event listener.
 */
public interface IEventListener {

    /**
     * On message received.
     *
     * @param event the event
     */
    void onMessageReceived(final Event event);
}
