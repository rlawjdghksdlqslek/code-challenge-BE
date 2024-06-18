package goorm.code_challenge.problem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.problem.domain.Problem;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
	List<Problem> findAllByRank(String rank);
	Problem findByTitle(String title);
}
