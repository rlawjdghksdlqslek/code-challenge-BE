package goorm.code_challenge.problem.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.problem.domain.Problem;
import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.problem.dto.ProblemRequest;
import goorm.code_challenge.problem.dto.ProblemResponse;
import goorm.code_challenge.problem.repository.ProblemRepository;
import goorm.code_challenge.ide.repository.TestCaseRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	private final TestCaseRepository testCaseRepository;

	public Problem createProblem(ProblemRequest problemRequest) {
		validateNumberOfTestCases(problemRequest);

		Problem problem = buildProblemFromRequest(problemRequest);
		problemRepository.save(problem);
		createTestCases(problemRequest.getTestCase(), problem);
		return problem;
	}

	private void validateNumberOfTestCases(ProblemRequest problemRequest) {
		if (problemRequest.getTestCase().size() < 10) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "테스트 케이스는 10개 이상을 입력해주세요");
		}
	}

	private Problem buildProblemFromRequest(ProblemRequest problemRequest) {
		Problem problem = new Problem();
		problem.setTitle(problemRequest.getTitle());
		problem.setContext(problemRequest.getContext());
		problem.setImage(problemRequest.getImage());
		problem.setRank(problemRequest.getRank());
		return problem;
	}

	private void createTestCases(Map<String, String> testCasesData, Problem problem) {
		for (Map.Entry<String, String> entry : testCasesData.entrySet()) {
			TestCase testCase = new TestCase(problem, entry.getKey(), entry.getValue());
			testCaseRepository.save(testCase);
		}
	}

	public List<ProblemResponse> getProblems(String rank) {
		List<ProblemResponse> problemResponses = new ArrayList<>();
		List<Problem> problems = problemRepository.findAllByRank(rank);
		if (problems.isEmpty()) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "해당 하는 문제가 없습니다");
		}
		for (Problem problem : problems) {
			ProblemResponse problemResponse = new ProblemResponse(problem);
			problemResponses.add(problemResponse);
		}
		return problemResponses;
	}

	public ProblemResponse getProblem(Long id) {
		Optional<Problem> problem = problemRepository.findById(id);
		if (problem.isEmpty()) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "해당 하는 문제가 없습니다");
		}

		return new ProblemResponse(problem.get());
	}
}
