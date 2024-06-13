package goorm.code_challenge.ide.repository;

import java.time.Period;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.ide.domain.Problem;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
	Problem findByProblemId(Long id);
}
