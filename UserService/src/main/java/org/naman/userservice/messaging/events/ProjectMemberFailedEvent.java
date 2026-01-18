package org.naman.userservice.messaging.events;
import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberFailedEvent {

    private String eventId;
    private String correlationId;
    private String taskId;
    private String projectId;
    private String userId;
    private String reason;
    private Instant occurredAt;
}
