package com.example.rabbitmq.publish;

import com.example.rabbitmq.domain.Event;
import org.springframework.amqp.core.Message;

/**
 * The interface Event sender.
 */
public interface IEventSender {

    /**
     * Buid message message.
     *
     * @param event the event
     * @return the message
     */
    Message buidMessage(final Event event);

    /**
     * Send message.
     *
     * @param event the event
     */
    void sendMessage(final Event event);
}
