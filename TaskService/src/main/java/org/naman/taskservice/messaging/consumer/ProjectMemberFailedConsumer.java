package org.naman.taskservice.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naman.taskservice.config.RabbitMqConfig;
import org.naman.taskservice.entity.Task;
import org.naman.taskservice.entity.enums.TaskStatus;
import org.naman.taskservice.messaging.events.ProjectMemberFailedEvent;
import org.naman.taskservice.repository.TaskRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectMemberFailedConsumer {

    private final TaskRepository taskRepository;

    @RabbitListener(queues = RabbitMqConfig.PROJECT_MEMBER_FAILED_QUEUE)
    @Transactional
    public void handle(ProjectMemberFailedEvent event) {

        Task task = taskRepository.findById(event.getTaskId())
                .orElseThrow(() -> {
                    log.error(
                            "Task not found for taskId={}, eventId={}",
                            event.getTaskId(),
                            event.getEventId()
                    );
                    return new IllegalStateException("Task not found");
                });

        // Idempotency
        if (task.getStatus() == TaskStatus.CANCELLED) {
            log.info(
                    "Ignoring duplicate ProjectMemberFailedEvent for taskId={}",
                    task.getId()
            );
            return;
        }

        // Invalid state (should never happen)
        if (task.getStatus() == TaskStatus.ACTIVE) {
            log.error(
                    "Received ProjectMemberFailedEvent after COMPLETED for taskId={}",
                    task.getId()
            );
            return; // or throw, depending on strictness
        }

        task.setStatus(TaskStatus.CANCELLED);
        taskRepository.save(task);

        log.info(
                "Saga rolled back: taskId={} cancelled due to project failure, eventId={}",
                task.getId(),
                event.getEventId()
        );
    }
}
