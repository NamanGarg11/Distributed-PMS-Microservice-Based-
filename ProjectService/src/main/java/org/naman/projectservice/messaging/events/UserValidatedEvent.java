package org.naman.projectservice.messaging.events;

import lombok.*;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserValidatedEvent {

    private String eventId;
    private String correlationId;
    private String taskId;
    private String userId;
    private String projectId;
    private Instant occurredAt;
}
