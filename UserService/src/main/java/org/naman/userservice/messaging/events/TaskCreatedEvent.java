package org.naman.userservice.messaging.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskCreatedEvent {
    private String eventId;
    private String correlationId;
    private String taskId;
    private String userId;
    private String projectId;
    private Instant occurredAt;
}
