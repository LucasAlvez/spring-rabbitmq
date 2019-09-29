package com.example.rabbitmq.subscribe;

import com.example.rabbitmq.domain.Event;
import com.example.rabbitmq.publish.IEventSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The type Event listener.
 */
@Component
public class EventListener implements IEventListener {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    @Autowired
    private IEventSender eventSender;

    @RabbitListener(queues = "${broker.queue}")
    @Override
    public void onMessageReceived(final Event event) {
        LOGGER.info("Message received: {}", event.toString());
        this.eventSender.sendMessage(event);
    }
}
