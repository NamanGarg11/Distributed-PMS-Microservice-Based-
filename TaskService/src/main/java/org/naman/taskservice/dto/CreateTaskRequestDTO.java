package org.naman.taskservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateTaskRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String userId;
    @NotBlank
    private String projectId;
}
