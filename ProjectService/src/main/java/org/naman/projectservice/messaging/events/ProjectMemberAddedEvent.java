package org.naman.projectservice.messaging.events;
import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectMemberAddedEvent {

    private String eventId;
    private String correlationId;
    private String taskId;
    private String projectId;
    private String userId;
    private Instant occurredAt;
}
