package org.naman.userservice.messaging.events;


import lombok.*;
import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCompensationCompletedEvent {

    private String eventId;
    private String correlationId;
    private String taskId;
    private String userId;
    private String projectId;
    private String reason;
    private Instant occurredAt;

}
