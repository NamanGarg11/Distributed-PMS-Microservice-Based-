package org.naman.taskservice.repository;

import org.naman.taskservice.entity.Task;
import org.naman.taskservice.entity.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByStatusAndSagaCompletedFalseAndSagaStartedAtBefore(
            TaskStatus status,
            Instant time
    );

}
