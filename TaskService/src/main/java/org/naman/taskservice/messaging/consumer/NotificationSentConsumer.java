package org.naman.taskservice.messaging.consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naman.taskservice.config.RabbitMqConfig;
import org.naman.taskservice.entity.Task;
import org.naman.taskservice.entity.enums.TaskStatus;
import org.naman.taskservice.messaging.events.NotificationSentEvent;
import org.naman.taskservice.repository.TaskRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSentConsumer {

    private final TaskRepository taskRepository;

    @RabbitListener(queues = RabbitMqConfig.NOTIFICATION_SENT_QUEUE)
    @Transactional
    public void handle(NotificationSentEvent event) {

        log.info(
                "[SAGA][NOTIFICATION_SENT_RECEIVED] correlationId={} taskId={}",
                event.getCorrelationId(),
                event.getTaskId()
        );

        Task task = taskRepository.findById(event.getTaskId())
                .orElse(null);

        if (task == null) {
            log.warn(
                    "[SAGA][TASK_NOT_FOUND] taskId={}",
                    event.getTaskId()
            );
            return;
        }

        // 🔒 Idempotency
        if (task.getStatus() == TaskStatus.ACTIVE) {
            log.info(
                    "[SAGA][IDEMPOTENT_SUCCESS] taskId={}",
                    task.getId()
            );
            return;
        }

        if (task.getStatus() == TaskStatus.CANCELLED) {
            log.warn(
                    "[SAGA][TASK_ALREADY_CANCELLED] taskId={}",
                    task.getId()
            );
            return;
        }

        task.setStatus(TaskStatus.ACTIVE);
        taskRepository.save(task);

        log.info(
                "[SAGA][TASK_COMPLETED] correlationId={} taskId={}",
                event.getCorrelationId(),
                task.getId()
        );
    }
}
