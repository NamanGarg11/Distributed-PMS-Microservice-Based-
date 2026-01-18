package org.naman.notificationservice.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.naman.notificationservice.config.RabbitMqConfig;
import org.naman.notificationservice.messaging.events.NotificationSentEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(NotificationSentEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE,
                "notification.sent",
                event
        );
    }
}
