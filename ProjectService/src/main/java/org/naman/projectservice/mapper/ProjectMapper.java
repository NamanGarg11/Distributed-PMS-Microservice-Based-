package org.naman.projectservice.mapper;

import org.naman.projectservice.entity.ProjectMember;
import org.naman.projectservice.messaging.events.UserValidatedEvent;
import org.naman.projectservice.messaging.events.ProjectMemberAddedEvent;
import org.naman.projectservice.messaging.events.ProjectMemberFailedEvent;

import java.time.Instant;
import java.util.UUID;

public class ProjectMapper {
    public static ProjectMemberFailedEvent UserValidToProjectFailed(UserValidatedEvent event){
        return ProjectMemberFailedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(event.getCorrelationId())
                .taskId(event.getTaskId())
                .projectId(event.getProjectId())
                .userId(event.getUserId())
                .reason("PROJECT_NOT_FOUND")
                .occurredAt(Instant.now())
                .build();
    }

    public static ProjectMemberAddedEvent UserValidToProjectAdded(UserValidatedEvent event){
        return  ProjectMemberAddedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(event.getCorrelationId())
                .taskId(event.getTaskId())
                .projectId(event.getProjectId())
                .userId(event.getUserId())
                .occurredAt(Instant.now())
                .build();
    }

    public static ProjectMember toProjectMember(UserValidatedEvent event){
        return ProjectMember.builder()
                .projectId(event.getProjectId())
                .userId(event.getUserId())
                .build();
    }
}
