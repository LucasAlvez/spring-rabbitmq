package com.example.rabbitmq.config;

import com.example.rabbitmq.subscribe.EventListener;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * The type Broker config.
 */
@Configuration
@ConfigurationProperties(prefix = "broker")
public class BrokerConfig {

    /**
     * Broker addresses.
     */
    private String addresses;

    /**
     * Broker exchange.
     */
    private String exchange;

    /**
     * Broker Routing key.
     */
    private String routingKey;

    /**
     * Broker Queue.
     */
    private String queue;

    /**
     * Broker username.
     */
    private String username;

    /**
     * Broker password.
     */
    private String password;

    /**
     * Configures the ConnectionFactory bean.
     *
     * @return An instance of ConnectionFactory.
     */
    @Bean
    public ConnectionFactory connectionFactory() {

        CachingConnectionFactory cf = new CachingConnectionFactory();
        cf.setPublisherConfirms(false);
        cf.setAddresses(this.addresses);
        cf.setUsername(this.username);
        cf.setPassword(this.password);
        return cf;
    }

    /**
     * Configures the RabbitTemplate bean.
     *
     * @param connectionFactory The ConnectionFactory to be used by the template.
     * @return An instance of RabbitTemplate.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
        retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
        rabbitTemplate.setRetryTemplate(retryTemplate);
        rabbitTemplate.setMessageConverter(this.converter());
        return rabbitTemplate;

    }

    /**
     * Converter jackson 2 json message converter.
     *
     * @return the jackson 2 json message converter
     */
    @Bean
    public Jackson2JsonMessageConverter converter() { return  new Jackson2JsonMessageConverter(); }

    /**
     * Event exchange topic exchange.
     *
     * @return the topic exchange
     */
    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(this.exchange);
    }

    /**
     * Queue queue.
     *
     * @return the queue
     */
    @Bean
    public Queue queue() {
        return new Queue(this.queue);
    }

    /**
     * Binding binding.
     *
     * @param queue         the queue
     * @param eventExchange the event exchange
     * @return the binding
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange eventExchange) {
        return BindingBuilder.bind(queue).to(eventExchange).with(this.routingKey);
    }

    /**
     * Event receiver event listener.
     *
     * @return the event listener
     */
    @Bean
    public EventListener eventReceiver() {
        return new EventListener();
    }

    /**
     * Gets addresses.
     *
     * @return the addresses
     */
    public String getAddresses() {
        return addresses;
    }

    /**
     * Sets addresses.
     *
     * @param addresses the addresses
     */
    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    /**
     * Gets exchange.
     *
     * @return the exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * Sets exchange.
     *
     * @param exchange the exchange
     */
    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    /**
     * Gets routing key.
     *
     * @return the routing key
     */
    public String getRoutingKey() {
        return routingKey;
    }

    /**
     * Sets routing key.
     *
     * @param routingKey the routing key
     */
    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    /**
     * Gets queue.
     *
     * @return the queue
     */
    public String getQueue() {
        return queue;
    }

    /**
     * Sets queue.
     *
     * @param queue the queue
     */
    public void setQueue(String queue) {
        this.queue = queue;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}

