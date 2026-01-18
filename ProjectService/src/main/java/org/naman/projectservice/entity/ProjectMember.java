package org.naman.projectservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "project_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ProjectMemberId.class)
public class ProjectMember {

    @Id
    private String projectId;

    @Id
    private String userId;
}
