package org.naman.userservice.messaging.consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naman.userservice.config.RabbitMqConfig;
import org.naman.userservice.messaging.events.*;
import org.naman.userservice.messaging.publisher.UserEventPublisher;
import org.naman.userservice.repository.UserRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.naman.userservice.entity.*;
import org.naman.userservice.entity.enums.*;
import org.naman.userservice.mappers.*;

@Component
@RequiredArgsConstructor
@Slf4j

public class ProjectMemberFailedConsumer {

    private final UserRepository userRepository;
    private final UserEventPublisher publisher;

   @RabbitListener(queues = RabbitMqConfig.PROJECT_MEMBER_FAILED_QUEUE)
@Transactional
public void handle(ProjectMemberFailedEvent event) {

    String correlationId = event.getCorrelationId();
    String userId = event.getUserId();

    log.warn(
        "[SAGA][PROJECT_MEMBER_FAILED_RECEIVED] correlationId={} userId={} projectId={}",
        correlationId, userId, event.getProjectId()
    );

    User user = userRepository.findById(userId).orElse(null);

    // ✅ Idempotent / already deleted / already compensated
    if (user == null) {
        log.warn(
            "[SAGA][USER_NOT_FOUND_IDEMPOTENT] correlationId={} userId={}",
            correlationId, userId
        );

        publisher.publishUserCompensationCompleted(
                UserMapper.toUserCompensationCompleted(event)
        );
        return;
    }

    if (user.getStatus() == UserStatus.VALIDATION_FAILED) {
        log.info(
            "[SAGA][IDEMPOTENT_COMPENSATION] correlationId={} userId={}",
            correlationId, userId
        );

        publisher.publishUserCompensationCompleted(
                UserMapper.toUserCompensationCompleted(event)
        );
        return;
    }

    // 🔁 COMPENSATION ACTION
    user.setStatus(UserStatus.VALIDATION_FAILED);
    userRepository.save(user);

    log.info(
        "[SAGA][USER_COMPENSATED] correlationId={} userId={}",
        correlationId, userId
    );

    publisher.publishUserCompensationCompleted(
            UserMapper.toUserCompensationCompleted(event)
    );

    log.info(
        "[SAGA][USER_COMPENSATION_COMPLETED_PUBLISHED] correlationId={}",
        correlationId
    );
}

}
