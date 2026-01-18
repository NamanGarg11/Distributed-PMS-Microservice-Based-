package org.naman.taskservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.naman.taskservice.dto.CreateTaskRequestDTO;
import org.naman.taskservice.dto.TaskResponseDTO;
import org.naman.taskservice.entity.Task;
import org.naman.taskservice.mappers.TaskMapper;
import org.naman.taskservice.messaging.events.TaskCreatedEvent;
import org.naman.taskservice.messaging.publisher.TaskEventPublisher;
import org.naman.taskservice.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskCommandService implements ITaskCommandService {
    private final TaskRepository taskRepository;

    private final TaskEventPublisher taskEventPublisher;
    @Override
    @Transactional
    public TaskResponseDTO createTask(CreateTaskRequestDTO createTaskRequest) {
        Task task= TaskMapper.RequestDtoToTask(createTaskRequest);
        taskRepository.save(task);

        String correlationId = java.util.UUID.randomUUID().toString();
// Mapper to create an event publisher
        TaskCreatedEvent event = TaskCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .correlationId(correlationId)
                .taskId(task.getId())
                .userId(task.getUserId())
                .projectId(task.getProjectId())
                .occurredAt(Instant.now())
                .build();

        taskEventPublisher.publish(event);
        log.info("Succeefully published the task to the queue with correlationId {} ",correlationId);
        return TaskMapper.TaskToResponseDTo(task);
    }


}
