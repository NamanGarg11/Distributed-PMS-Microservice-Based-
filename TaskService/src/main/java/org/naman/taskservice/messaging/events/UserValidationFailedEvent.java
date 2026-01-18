package org.naman.taskservice.messaging.events;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationFailedEvent {

    private String eventId;
    private String correlationId;
    private String taskId;
    private String userId;
    private String reason;
    private Instant occurredAt;
}
