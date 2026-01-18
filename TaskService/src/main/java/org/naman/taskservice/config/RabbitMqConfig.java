package org.naman.taskservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String SAGA_EXCHANGE = "saga.exchange";

    public static final String TASK_CREATED_QUEUE = "task.created.queue";
    public static final String TASK_CREATED_ROUTING_KEY = "task.created";

    public static final String PROJECT_MEMBER_ADDED_QUEUE = "task.project.member.added.queue";
    public static final String PROJECT_MEMBER_FAILED_QUEUE ="task.project.member.failed.queue";

    public static final String USER_VALIDATION_FAILED_QUEUE = "task.user.validation.failed.queue";
    public static final String USER_COMPENSATION_COMPLETED_QUEUE = "task.user.compensation.completed.queue";

    public static final String TASK_DLQ = "task.dlq";
    public static final String TASK_DLQ_EXCHANGE = "task.dlx";

    public static final String NOTIFICATION_SENT_QUEUE = "task.notification.sent.queue";


    @Bean
    public TopicExchange sagaExchange() {
        return new TopicExchange(SAGA_EXCHANGE);
    }

    @Bean
    public Queue taskCreatedQueue() {
        return QueueBuilder.durable(TASK_CREATED_QUEUE)
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
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }

    // Project Member Success or Failed
    @Bean
    public Queue projectMemberAddedQueue() {
        return QueueBuilder.durable(PROJECT_MEMBER_ADDED_QUEUE).build();
    }

    @Bean
    public Queue projectMemberFailedQueue() {
        return QueueBuilder.durable(PROJECT_MEMBER_FAILED_QUEUE).build();
    }

    @Bean
    public Binding projectMemberAddedBinding(TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(projectMemberAddedQueue())
                .to(sagaExchange)
                .with("project.member.added");
    }

    @Bean
    public Binding projectMemberFailedBinding(TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(projectMemberFailedQueue())
                .to(sagaExchange)
                .with("project.member.add.failed");
    }

    // User Validation Failed
    @Bean
    public Queue userValidationFailedQueue() {
        return QueueBuilder.durable(USER_VALIDATION_FAILED_QUEUE)
                .withArgument("x-dead-letter-exchange", TASK_DLQ_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", TASK_DLQ)
                .withArgument("x-max-delivery-count", 3) // RabbitMQ 3.12+
                .build();
    }


    @Bean
    public Binding userValidationFailedBinding(TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(userValidationFailedQueue())
                .to(sagaExchange)
                .with("user.validation.failed");
    }
    @Bean
    public Queue userCompensationCompletedQueue() {
        return QueueBuilder.durable(USER_COMPENSATION_COMPLETED_QUEUE).build();
    }

    // user dlq
    @Bean
    public DirectExchange taskDlx() {
        return new DirectExchange(TASK_DLQ_EXCHANGE);
    }
    @Bean
    public Queue taskDlq() {
        return QueueBuilder.durable(TASK_DLQ).build();
    }
    @Bean
    public Binding taskDlqBinding() {
        return BindingBuilder
                .bind(taskDlq())
                .to(taskDlx())
                .with(TASK_DLQ);
    }
    @Bean
    Queue notificationSentQueue() {
        return QueueBuilder.durable(NOTIFICATION_SENT_QUEUE).build();
    }

    @Bean
    Binding notificationSentBinding(Queue notificationSentQueue, TopicExchange sagaExchange) {
        return BindingBuilder
                .bind(notificationSentQueue)
                .to(sagaExchange)
                .with("notification.sent");
    }


}
