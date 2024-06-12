package goorm.code_challenge.ide.application;

import java.io.IOException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import goorm.code_challenge.ide.dto.CodeSubmission;
import goorm.code_challenge.ide.utils.JavaJudge;
import goorm.code_challenge.ide.utils.JudgeUtil;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JudgeService {
	private final UserRepository userRepository;

	public void judge(CodeSubmission dto, UserDetails userDetails) throws IOException {
		final User user = userRepository.findByLoginId(userDetails.getUsername());
		JudgeUtil judgeUtil = new JavaJudge();
		judgeUtil.executeCode(dto.getCode());

	}
}
