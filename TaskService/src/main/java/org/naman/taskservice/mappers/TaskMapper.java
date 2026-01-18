package org.naman.taskservice.mappers;

import org.naman.taskservice.dto.CreateTaskRequestDTO;
import org.naman.taskservice.dto.TaskResponseDTO;
import org.naman.taskservice.entity.Task;

public class TaskMapper {
    public static Task RequestDtoToTask(CreateTaskRequestDTO task){
       return  Task.builder()
                .title(task.getTitle())
                .userId(task.getUserId())
                .projectId(task.getProjectId())
                    .build();
    }
    public static TaskResponseDTO TaskToResponseDTo(Task task){
        return TaskResponseDTO.builder()
                .id(task.getId())
                .build();
    }
}
