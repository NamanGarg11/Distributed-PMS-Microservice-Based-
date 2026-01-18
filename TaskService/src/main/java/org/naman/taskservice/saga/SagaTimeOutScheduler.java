package org.naman.taskservice.saga;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naman.taskservice.entity.Task;
import org.naman.taskservice.entity.enums.TaskStatus;
import org.naman.taskservice.repository.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SagaTimeOutScheduler{

    private final TaskRepository taskRepository;

    // runs every 30 seconds
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void cancelStuckSagas() {

        Instant timeoutThreshold = Instant.now().minusSeconds(20); // waits for 20 seconds

        List<Task> stuckTasks =
                taskRepository
                        .findByStatusAndSagaCompletedFalseAndSagaStartedAtBefore(
                                TaskStatus.PENDING,
                                timeoutThreshold
                        );

        for (Task task : stuckTasks) {
            log.error(
                    "[SAGA][TIMEOUT] Cancelling stuck taskId={} sagaStartedAt={}",
                    task.getId(),
                    task.getSagaStartedAt()
            );

            task.setStatus(TaskStatus.CANCELLED);
            task.setSagaCompleted(true);
            taskRepository.save(task);
        }
    }
}
