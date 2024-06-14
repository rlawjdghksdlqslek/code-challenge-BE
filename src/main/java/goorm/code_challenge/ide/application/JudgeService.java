package goorm.code_challenge.ide.application;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.ide.dto.CodeSubmission;
import goorm.code_challenge.problem.repository.ProblemRepository;
import goorm.code_challenge.ide.repository.TestCaseRepository;
import goorm.code_challenge.ide.utils.JavaJudge;
import goorm.code_challenge.ide.utils.JudgeUtil;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeService {
	private final UserRepository userRepository;
	private final ProblemRepository problemRepository;
	private final TestCaseRepository testCaseRepository;

	public Map<String, String> judge(CodeSubmission dto){
		List<TestCase> testCases = testCaseRepository.findAllByProblemId(dto.getProblemId());
		if(testCases.isEmpty()){
			throw new CustomException(ErrorCode.BAD_REQUEST,"해당 문제를 찾을 수 없습니다.");
		}


		JudgeUtil judgeUtil = new JavaJudge();
		return judgeUtil.executeCode(dto.getCode(), testCases);
	}
}
