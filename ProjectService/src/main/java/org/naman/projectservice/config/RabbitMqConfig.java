package org.naman.projectservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";

    public static final String USER_VALIDATED_QUEUE = "project.user.validated.queue";
    public static final String USER_VALIDATED_ROUTING_KEY = "user.validated";

    public static final String PROJECT_DLQ = "project.dlq";
    public static final String PROJECT_DLX = "project.dlx";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue userValidatedQueue() {
        return QueueBuilder.durable(USER_VALIDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", PROJECT_DLX)
                .withArgument("x-dead-letter-routing-key", PROJECT_DLQ)
                .build();
    }


    @Bean
    public Binding userValidatedBinding() {
        return BindingBuilder
                .bind(userValidatedQueue())
                .to(sagaExchange())
                .with(USER_VALIDATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    // dlq
    @Bean
    public DirectExchange projectDlx() {
        return new DirectExchange(PROJECT_DLX);
    }

    @Bean
    public Queue projectDlq() {
        return QueueBuilder.durable(PROJECT_DLQ).build();
    }

    @Bean
    public Binding projectDlqBinding() {
        return BindingBuilder
                .bind(projectDlq())
                .to(projectDlx())
                .with(PROJECT_DLQ);
    }

}

