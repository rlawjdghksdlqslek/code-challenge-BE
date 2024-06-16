package goorm.code_challenge.ide.application;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.ide.dto.reuest.CodeSubmission;
import goorm.code_challenge.ide.utils.run.JavaScriptJudge;
import goorm.code_challenge.ide.utils.run.PythonJudge;
import goorm.code_challenge.problem.repository.ProblemRepository;
import goorm.code_challenge.ide.repository.TestCaseRepository;
import goorm.code_challenge.ide.utils.run.JavaJudge;
import goorm.code_challenge.ide.utils.run.JudgeUtil;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeService {
	private final UserRepository userRepository;
	private final ProblemRepository problemRepository;
	private final TestCaseRepository testCaseRepository;

	public Map<String, String> judge(CodeSubmission dto) {
		List<TestCase> testCases = testCaseRepository.findAllByProblemId(dto.getProblemId());
		JudgeUtil judgeUtil;
		if (testCases.isEmpty()) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "해당 문제를 찾을 수 없습니다.");
		}
		judgeUtil = switch (dto.getCompileLanguage()) {
			case "java" -> new JavaJudge();
			case "python" -> new PythonJudge();
			case "javascript" -> new JavaScriptJudge();
			default -> throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 언어 선택입니다.");
		};

		return judgeUtil.executeCode(dto.getCode(), testCases);
	}
}
