package com.example.rabbitmq.publish;

import com.example.rabbitmq.config.BrokerConfig;
import com.example.rabbitmq.domain.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class EventSender implements IEventSender {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EventSender.class);

    /**
     * Rabbit Template.
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Broker Config.
     */
    @Autowired
    private BrokerConfig brokerConfig;

    /**
     * Application Name.
     */
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Message Name.
     */
    private static final String MESSAGE_NAME = "message.v1";

    /**
     * Message Version.
     */
    private static final String MESSAGE_VERSION = "v1";

    @Override
    public Message buidMessage(final Event event) {
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setAppId(this.applicationName);
        messageProperties.setContentType("application/json");
        messageProperties.setContentEncoding("UTF-8");
        messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        messageProperties.setMessageId(UUID.randomUUID().toString());
        messageProperties.setType("event");
        messageProperties.setPriority(0);
        messageProperties.setTimestamp(new Date());

        // Populate message headers.
        messageProperties.getHeaders().put("message_name", MESSAGE_NAME);
        messageProperties.getHeaders().put("message_version", MESSAGE_VERSION);

        Message message = null;

        try {
            message = new Message(new ObjectMapper().writeValueAsBytes(event), messageProperties);
            return message;
        } catch (JsonProcessingException ex) {
            LOGGER.error("Error converting gateway log message to JSON format. {}", ex);
        }
        return null;
    }

    @Override
    public void sendMessage(final Event event) {
        this.buidMessage(event);
        this.rabbitTemplate.send(this.brokerConfig.getExchange(), this.brokerConfig.getRoutingKey(), this.buidMessage(event));

        LOGGER.info("Event sent to the queue: {}", this.brokerConfig.getRoutingKey());
    }
}
