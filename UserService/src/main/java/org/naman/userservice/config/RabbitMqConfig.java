package org.naman.userservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Configuration
public class RabbitMqConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";

    public static final String TASK_CREATED_QUEUE = "user.task.created.queue";
    public static final String TASK_CREATED_ROUTING_KEY = "task.created";


    public static final String PROJECT_MEMBER_FAILED_QUEUE =  "user.project.member.failed.queue";

    public static final String PROJECT_MEMBER_FAILED_ROUTING_KEY = "project.member.failed";

    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue taskCreatedQueue() {
        return QueueBuilder.durable(TASK_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", SAGA_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", "task.created.dlq")
                .build();
    }

    @Bean
    public Binding taskCreatedBinding() {
        return BindingBuilder
                .bind(taskCreatedQueue())
                .to(sagaExchange())
                .with(TASK_CREATED_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

     @Bean
    public Queue projectMemberFailedQueue() {
        return QueueBuilder.durable(PROJECT_MEMBER_FAILED_QUEUE).build();
    }

    @Bean
    public Binding projectMemberFailedBinding() {
        return BindingBuilder
                .bind(projectMemberFailedQueue())
                .to(sagaExchange())
                .with(PROJECT_MEMBER_FAILED_ROUTING_KEY);
    }


    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jacksonMessageConverter());
        return template;
    }
}
