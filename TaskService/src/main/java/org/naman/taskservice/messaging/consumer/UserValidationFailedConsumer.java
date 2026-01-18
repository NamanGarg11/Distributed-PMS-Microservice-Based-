package org.naman.taskservice.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naman.taskservice.config.RabbitMqConfig;
import org.naman.taskservice.entity.Task;
import org.naman.taskservice.entity.enums.TaskStatus;
import org.naman.taskservice.messaging.events.UserValidationFailedEvent;
import org.naman.taskservice.repository.TaskRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserValidationFailedConsumer {

    private final TaskRepository taskRepository;

    @RabbitListener(queues = RabbitMqConfig.USER_VALIDATION_FAILED_QUEUE)
    @Transactional
    public void handle(UserValidationFailedEvent event) {

        Task task = taskRepository.findById(event.getTaskId())
                .orElseThrow(() -> {
                    log.error(
                            "Task not found for UserValidationFailedEvent. taskId={}, eventId={}",
                            event.getTaskId(),
                            event.getEventId()
                    );
                    return new IllegalStateException("Task not found");
                });

        // Idempotency
        if (task.getStatus() == TaskStatus.CANCELLED) {
            log.info(
                    "Ignoring duplicate UserValidationFailedEvent for taskId={}",
                    task.getId()
            );
            return;
        }

        // Invalid saga state (should never happen)
        if (task.getStatus() == TaskStatus.ACTIVE) {
            log.error(
                    "UserValidationFailedEvent received after task COMPLETED. taskId={}, eventId={}",
                    task.getId(),
                    event.getEventId()
            );
            return; // or throw, depending on strictness
        }

        task.setStatus(TaskStatus.CANCELLED);
        taskRepository.save(task);

        log.info(
                "Saga compensation applied: taskId={} cancelled due to user validation failure. reason={}",
                task.getId(),
                event.getReason()
        );
    }
}
