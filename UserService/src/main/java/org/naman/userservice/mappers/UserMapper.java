package org.naman.userservice.mappers;

import org.naman.userservice.messaging.events.ProjectMemberFailedEvent;
import org.naman.userservice.messaging.events.TaskCreatedEvent;
import org.naman.userservice.messaging.events.UserCompensationCompletedEvent;
import org.naman.userservice.messaging.events.UserValidatedEvent;
import org.naman.userservice.messaging.events.UserValidationFailedEvent;

import java.time.Instant;
import java.util.UUID;

public class UserMapper {
    public static UserValidatedEvent TaskToUserEvent(TaskCreatedEvent event){
        return UserValidatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(event.getCorrelationId())
                .taskId(event.getTaskId())
                .userId(event.getUserId())
                .projectId(event.getProjectId())
                .occurredAt(Instant.now())
                .build();
    }

    public static UserValidationFailedEvent TaskToUserFailedEvent(TaskCreatedEvent event){
      return   UserValidationFailedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(event.getCorrelationId())
                .taskId(event.getTaskId())
                .userId(event.getUserId())
                .reason("USER_NOT_FOUND")
                .occurredAt(Instant.now())
                .build();
    }
 public static UserCompensationCompletedEvent toUserCompensationCompleted(ProjectMemberFailedEvent event) {
        return UserCompensationCompletedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(event.getCorrelationId())
                .userId(event.getUserId())
                .projectId(event.getProjectId())
                .occurredAt(Instant.now())
                .build();
    }
}
