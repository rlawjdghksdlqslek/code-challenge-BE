package goorm.code_challenge.ide.application;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.Problem;
import goorm.code_challenge.ide.dto.CodeSubmission;
import goorm.code_challenge.ide.repository.ProblemRepository;
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

	public Map<String, String> judge(CodeSubmission dto, UserDetails userDetails) throws IOException, InterruptedException {
		final User user = userRepository.findByLoginId(userDetails.getUsername());
		Problem problem = problemRepository.findById(dto.getProblemId())
			.orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST,"해당 문제를 찾을 수 없습니다."));

		JudgeUtil judgeUtil = new JavaJudge();
		return judgeUtil.executeCode(dto.getCode(), problem);
	}
}
