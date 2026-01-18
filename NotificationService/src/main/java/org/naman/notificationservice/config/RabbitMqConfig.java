package org.naman.notificationservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";

    public static final String PROJECT_MEMBER_ADDED_QUEUE =
            "notification.project.member.added.queue";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue projectMemberAddedQueue() {
        return QueueBuilder.durable(PROJECT_MEMBER_ADDED_QUEUE).build();
    }

    @Bean
    public Binding projectMemberAddedBinding() {
        return BindingBuilder
                .bind(projectMemberAddedQueue())
                .to(sagaExchange())
                .with("project.member.added");
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
