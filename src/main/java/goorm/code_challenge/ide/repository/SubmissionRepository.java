package goorm.code_challenge.ide.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.ide.domain.Submission;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
}
