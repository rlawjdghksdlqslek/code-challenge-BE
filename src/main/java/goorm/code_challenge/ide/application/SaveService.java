package goorm.code_challenge.ide.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.global.utils.FileToString;
import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.ide.dto.reponse.CodePathDto;
import goorm.code_challenge.ide.dto.reuest.CodeSubmission;
import goorm.code_challenge.ide.dto.reuest.FeedbackRequest;
import goorm.code_challenge.ide.dto.reponse.FeedbackResponse;
import goorm.code_challenge.ide.repository.SubmissionRepository;
import goorm.code_challenge.ide.repository.TestCaseRepository;
import goorm.code_challenge.ide.utils.JavaSave;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaveService {
	private final UserRepository userRepository;
	private final SubmissionRepository submissionRepository;
	private final TestCaseRepository testCaseRepository;
	public Submission saveCode(CodeSubmission dto, UserDetails userDetails){
		final User user = userRepository.findByLoginId(userDetails.getUsername());
		List<TestCase> testCases = testCaseRepository.findAllByProblemId(dto.getProblemId());
		if(testCases.isEmpty()){
			throw new CustomException(ErrorCode.BAD_REQUEST,"해당 문제를 찾을 수 없습니다.");
		}
		Submission checkExists = submissionRepository.findByRoomIdAndProblemIdAndUserId(
			dto.getRoomId(), dto.getProblemId(), user.getId());
		if(checkExists!=null){
			throw new CustomException(ErrorCode.BAD_REQUEST,"제출은 한번만 가능합니다");
		}

		JavaSave javaSave = new JavaSave();
		CodePathDto codePathDto = javaSave.saveCode(dto.getCode(), testCases);
		Submission submission = new Submission();
		submission.setUserId(user.getId());
		submission.setRoomId(dto.getRoomId());
		submission.setProblemId(dto.getProblemId());
		submission.setCodePath(codePathDto.getFilePath());
		submission.setSolve(codePathDto.isSolved());
		submission.setSubmitTime(LocalDateTime.now());
		submissionRepository.save(submission);

		return submission;
	}
	public List<FeedbackResponse> getCode(FeedbackRequest feedbackRequest){
		Long room = feedbackRequest.getRoomId();
		Long problem = feedbackRequest.getProblemId();
		List<Submission> submission = submissionRepository.findAllByRoomIdAndProblemId(room, problem);
		FileToString fileToString = new FileToString();
		List<FeedbackResponse> feedbackResponses = new ArrayList<>();
		if(submission==null){
			throw new CustomException(ErrorCode.BAD_REQUEST,"코드를 찾을 수 없습니다");
		}
		for(Submission sub:submission){
			Optional<User> user = userRepository.findById(sub.getUserId());
			if(user.isEmpty()) throw new CustomException(ErrorCode.BAD_REQUEST,"유저를 찾을 수 없습니다");
			feedbackResponses.add(new FeedbackResponse(user.get().getName(),fileToString.changeFile(sub.getCodePath())));
		}

		return feedbackResponses;
	}
}
