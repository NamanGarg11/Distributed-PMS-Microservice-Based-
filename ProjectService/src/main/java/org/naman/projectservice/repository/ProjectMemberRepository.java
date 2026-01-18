package org.naman.projectservice.repository;

import org.naman.projectservice.entity.ProjectMember;
import org.naman.projectservice.entity.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    boolean existsByProjectIdAndUserId(String projectId, String userId);
}

