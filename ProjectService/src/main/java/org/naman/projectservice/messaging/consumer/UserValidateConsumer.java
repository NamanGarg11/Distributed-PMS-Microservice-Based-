package org.naman.projectservice.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naman.projectservice.config.RabbitMqConfig;
import org.naman.projectservice.entity.ProjectMember;
import org.naman.projectservice.messaging.events.*;
import org.naman.projectservice.mapper.ProjectMapper;
import org.naman.projectservice.messaging.Publisher.ProjectEventPublisher;
import org.naman.projectservice.repository.ProjectMemberRepository;
import org.naman.projectservice.repository.ProjectRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
@Component
@RequiredArgsConstructor
@Slf4j

public class UserValidateConsumer {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectEventPublisher publisher;

    @RabbitListener(queues = RabbitMqConfig.USER_VALIDATED_QUEUE)
    @Transactional
    public void handle(UserValidatedEvent event) {

        String correlationId = event.getCorrelationId();

        log.info(
            "[SAGA][USER_VALIDATED_RECEIVED] correlationId={} event={}",
            correlationId, event
        );

        // ❌ Invalid payload → poison message → DLQ
        if (event.getProjectId() == null || event.getUserId() == null) {
            log.error(
                "[SAGA][INVALID_EVENT] correlationId={} projectId={} userId={}",
                correlationId, event.getProjectId(), event.getUserId()
            );
           
    ProjectMemberFailedEvent failedEvent =
            ProjectMapper.UserValidToProjectFailed(event);

    publisher.publishMemberAddFailed(failedEvent);

    log.info(
        "[SAGA][PROJECT_MEMBER_ADD_FAILED_PUBLISHED] correlationId={}",
        correlationId
    );
    return;
        }

        String projectId = event.getProjectId();
        String userId = event.getUserId();

        // ❌ Project not found → business failure
        if (!projectRepository.existsById(projectId)) {
            log.warn(
                "[SAGA][PROJECT_NOT_FOUND] correlationId={} projectId={}",
                correlationId, projectId
            );

            ProjectMemberFailedEvent failedEvent =
                    ProjectMapper.UserValidToProjectFailed(event);

            publisher.publishMemberAddFailed(failedEvent);

            log.info(
                "[SAGA][PROJECT_MEMBER_ADD_FAILED_PUBLISHED] correlationId={}",
                correlationId
            );
            return;
        }

        // ✅ Idempotency check
        boolean alreadyMember =
                projectMemberRepository.existsByProjectIdAndUserId(
                        projectId, userId
                );

        if (alreadyMember) {
            log.info(
                "[SAGA][IDEMPOTENT_SUCCESS] correlationId={} projectId={} userId={}",
                correlationId, projectId, userId
            );

            ProjectMemberAddedEvent successEvent =
                    ProjectMapper.UserValidToProjectAdded(event);

            publisher.publishMemberAdded(successEvent);

            log.info(
                "[SAGA][PROJECT_MEMBER_ADDED_PUBLISHED] correlationId={}",
                correlationId
            );
            return;
        }

        // ✅ Save new member
        ProjectMember member = ProjectMapper.toProjectMember(event);
        projectMemberRepository.save(member);

        log.info(
            "[SAGA][PROJECT_MEMBER_SAVED] correlationId={} projectId={} userId={}",
            correlationId, projectId, userId
        );

        ProjectMemberAddedEvent successEvent =
                ProjectMapper.UserValidToProjectAdded(event);

        publisher.publishMemberAdded(successEvent);

        log.info(
            "[SAGA][PROJECT_MEMBER_ADDED_PUBLISHED] correlationId={}",
            correlationId
        );
    }
}
