package goorm.code_challenge.ide.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.ide.domain.Submission;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
	List<Submission> findAllByRoomIdAndProblemId(Long roomId,Long problemId);
	Submission findByRoomIdAndProblemIdAndUserId(Long roomId,Long problemId,Long userId);
}
