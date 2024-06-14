package goorm.code_challenge.ide.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.ide.domain.TestCase;

public interface TestCaseRepository extends JpaRepository<TestCase,Long> {
	List<TestCase> findAllByProblemId(Long Id);
}
