package org.naman.taskservice.service;

import org.naman.taskservice.dto.CreateTaskRequestDTO;
import org.naman.taskservice.dto.TaskResponseDTO;

public interface ITaskCommandService {
    TaskResponseDTO createTask(CreateTaskRequestDTO createTaskRequest);
}
