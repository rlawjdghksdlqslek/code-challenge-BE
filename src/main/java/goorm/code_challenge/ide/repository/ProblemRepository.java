package goorm.code_challenge.ide.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.ide.domain.Problem;

public interface ProblemRepository extends JpaRepository<Problem,Long> {
	List<Problem> findAllByRank(String rank);
}
