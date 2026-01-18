package org.naman.taskservice.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.naman.taskservice.config.RabbitMqConfig;
import org.naman.taskservice.messaging.events.TaskCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TaskEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void publish(TaskCreatedEvent event) {
        rabbitTemplate.convertAndSend( RabbitMqConfig.SAGA_EXCHANGE,
                RabbitMqConfig.TASK_CREATED_ROUTING_KEY,
                event);
    }
}


/*This publisher
Trigger RabbitMQ connection

Trigger exchange declaration

Trigger queue binding
 */