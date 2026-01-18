package org.naman.userservice.messaging.publisher;

import lombok.RequiredArgsConstructor;
import org.naman.userservice.config.RabbitMqConfig;
import org.naman.userservice.messaging.events.UserCompensationCompletedEvent;
import org.naman.userservice.messaging.events.UserValidatedEvent;
import org.naman.userservice.messaging.events.UserValidationFailedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserValidated(UserValidatedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE,
                "user.validated",
                event
        );
    }

    public void publishUserValidationFailed(UserValidationFailedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE,
                "user.validation.failed",
                event
        );
    }
     public void publishUserCompensationCompleted(UserCompensationCompletedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE,
                "user.compensation.completed",
                event
        );
    }
}
