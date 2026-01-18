package org.naman.taskservice.messaging.events;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSentEvent {

    private String eventId;
    private String correlationId;
    private String taskId;
    private String userId;
    private String projectId;
    private String channel;     // EMAIL / PUSH / LOG
    private Instant occurredAt;
}

