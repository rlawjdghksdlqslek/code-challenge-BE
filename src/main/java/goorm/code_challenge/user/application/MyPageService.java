package goorm.code_challenge.user.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.ide.repository.SubmissionRepository;
import goorm.code_challenge.problem.domain.Problem;
import goorm.code_challenge.problem.repository.ProblemRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.dto.response.MyCodes;
import goorm.code_challenge.user.dto.response.MyPageResponse;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
	private final UserRepository userRepository;
	private final SubmissionRepository submissionRepository;
	private final ProblemRepository problemRepository;
	public MyPageResponse getMyInfo(){
		User user = getCurrentUser();
		List<Submission> submissions = submissionRepository.findAllByUserId(user.getId());
		List<MyCodes> myCodes = new ArrayList<>();
		if(submissions==null){
			return new MyPageResponse(user,null);
		}
		for(Submission submission :submissions){
			Optional<Problem> problem = problemRepository.findById(submission.getProblemId());
			myCodes.add(new MyCodes(submission,problem.get()));
		}
		return new MyPageResponse(user,myCodes);

	}
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUserName = authentication.getName();
		User currentUser = userRepository.findByLoginId(currentUserName);

		if (currentUser == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED, "현재 사용자를 찾을 수 없습니다.");
		}
		return currentUser;
	}
}
