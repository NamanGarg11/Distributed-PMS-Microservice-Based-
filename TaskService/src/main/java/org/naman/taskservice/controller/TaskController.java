package org.naman.taskservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naman.taskservice.dto.CreateTaskRequestDTO;
import org.naman.taskservice.dto.TaskResponseDTO;
import org.naman.taskservice.service.ITaskCommandService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final ITaskCommandService taskCommandService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@Valid @RequestBody CreateTaskRequestDTO request) {
        return taskCommandService.createTask(request);
    }

}
