package org.naman.taskservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.naman.taskservice.entity.enums.TaskStatus;
import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "tasks")
public class Task {
        @Id
        @Column(length = 36)
        private String id;

        private String title;

        @Column(nullable = false)
        private String userId;

        @Column(nullable = false)
        private String projectId;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TaskStatus status;

        @Column(nullable = false, updatable = false)
        private Instant createdAt;

        @Column(nullable = false)
        private Instant sagaStartedAt;

        @Column(nullable = false)
        private boolean sagaCompleted;

        @PrePersist
        public void prePersist() {
                if (this.id == null) {
                        this.id = java.util.UUID.randomUUID().toString();
                }
                if (this.status == null) {
                        this.status = TaskStatus.PENDING;
                }
                if (this.createdAt == null) {
                        this.createdAt = Instant.now();
                }
                if (this.sagaStartedAt == null) {
                        this.sagaStartedAt = Instant.now();
                }
                this.sagaCompleted = false;
        }

}
