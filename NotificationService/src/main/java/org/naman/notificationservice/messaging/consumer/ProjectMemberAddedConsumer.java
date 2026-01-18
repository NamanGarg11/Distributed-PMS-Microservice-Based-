package org.naman.notificationservice.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naman.notificationservice.config.RabbitMqConfig;
import org.naman.notificationservice.messaging.events.NotificationSentEvent;
import org.naman.notificationservice.messaging.events.ProjectMemberAddedEvent;
import org.naman.notificationservice.messaging.publisher.NotificationPublisher;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectMemberAddedConsumer {

    private final NotificationPublisher publisher;

    @RabbitListener(
            queues = RabbitMqConfig.PROJECT_MEMBER_ADDED_QUEUE
    )
    public void handle(ProjectMemberAddedEvent event) {

        // 🔔 Simulated notification
        log.info(
                "Notification: User {} added to Project {} for Task {}",
                event.getUserId(),
                event.getProjectId(),
                event.getTaskId()
        );

        NotificationSentEvent sentEvent =
                NotificationSentEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .correlationId(event.getCorrelationId())
                        .taskId(event.getTaskId())
                        .userId(event.getUserId())
                        .projectId(event.getProjectId())
                        .channel("LOG")
                        .occurredAt(Instant.now())
                        .build();

        publisher.publish(sentEvent);

        log.info("NotificationSent event published");
    }
}
