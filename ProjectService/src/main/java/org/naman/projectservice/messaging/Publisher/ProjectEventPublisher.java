package org.naman.projectservice.messaging.Publisher;

import lombok.RequiredArgsConstructor;
import org.naman.projectservice.config.RabbitMqConfig;
import org.naman.projectservice.messaging.events.ProjectMemberAddedEvent;
import org.naman.projectservice.messaging.events.ProjectMemberFailedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishMemberAdded(ProjectMemberAddedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE,
                "project.member.added",
                event
        );
    }

    public void publishMemberAddFailed(ProjectMemberFailedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.SAGA_EXCHANGE,
                "project.member.add.failed",
                event
        );
    }
}
