package goorm.code_challenge.ide.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.ide.domain.Submission;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
	List<Submission> findAllByRoomIdAndProblemId(Long roomId,Long problemId);
	List<Submission> findAllByUserId(Long userId);
	Submission findByRoomIdAndProblemIdAndUserId(Long roomId,Long problemId,Long userId);
	List<Submission> findAllByUserIdAndIsSolve(Long userId, boolean isSolved);
}
