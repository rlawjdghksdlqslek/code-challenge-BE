package goorm.code_challenge.user.application;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.global.utils.S3Upload;
import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.ide.repository.SubmissionRepository;
import goorm.code_challenge.problem.domain.Problem;
import goorm.code_challenge.problem.repository.ProblemRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.dto.request.UserNameUpdateRequest;
import goorm.code_challenge.user.dto.response.MyCodes;
import goorm.code_challenge.user.dto.response.MyPageResponse;
import goorm.code_challenge.user.dto.response.RankingResponse;
import goorm.code_challenge.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyPageService {
	private final UserRepository userRepository;
	private final SubmissionRepository submissionRepository;
	private final ProblemRepository problemRepository;
	private final S3Upload s3Uploader;
	public MyPageResponse getMyInfo(){
		User user = getCurrentUser();
		List<Submission> submissions = submissionRepository.findAllByUserId(user.getId());
		List<MyCodes> myCodes = new ArrayList<>();
		int userRank = getUserRank(user.getId());
		if(submissions==null){
			return new MyPageResponse(user,null,userRank);
		}
		for(Submission submission :submissions){
			Optional<Problem> problem = problemRepository.findById(submission.getProblemId());
			myCodes.add(new MyCodes(submission,problem.get()));
		}
		return new MyPageResponse(user,myCodes,userRank);

	}
	public int getUserRank(Long userId) {
		// exp_points를 기준으로 모든 유저를 내림차순으로 정렬하여 가져옵니다.
		List<User> users = userRepository.findAllByOrderByExpPointsDesc();

		// 유저의 순위를 계산합니다.
		int rank = 1;
		for (User user : users) {
			if (user.getId().equals(userId)) {
				return rank;
			}
			rank++;
		}
		return -1; // 유저를 찾을 수 없는 경우
	}
	@Transactional
	public void updateName(UserNameUpdateRequest userNameUpdateRequest, Errors errors){
		if (errors.hasErrors()) {
			for (FieldError error : errors.getFieldErrors()) {
				throw new CustomException(ErrorCode.BAD_REQUEST, error.getDefaultMessage());
			}
		}
		if(userRepository.existsByName(userNameUpdateRequest.getUserName())){
			throw new CustomException(ErrorCode.BAD_REQUEST, "이미 존재하는 이름입니다");
		}
		User currentUser = getCurrentUser();
		currentUser.setName(userNameUpdateRequest.getUserName());
	}

	@Transactional
	public void updateImage(MultipartFile image) throws IOException {
		System.out.println("문제 확인");
		User user = getCurrentUser();
		String userImageURL = user.getProfileImage();
		if(userImageURL!=null){
			s3Uploader.fileDelete(userImageURL);
		}
		String postImg = s3Uploader.upload(image, "images");
		user.setProfileImage(postImg);
	}
	public List<RankingResponse> getRanking(){
		List<User> users = userRepository.findTop10ByOrderByExpPointsDesc();
		List<RankingResponse> rankingResponses = new ArrayList<>();
		int rank = 1;
		for (User user:users){
			int countSolved = countSolvedProblem(user);
			rankingResponses.add(new RankingResponse(user,rank,countSolved));
			rank++;
		}
		return rankingResponses;
	}
	public Integer countSolvedProblem(User user){
		List<Submission> submissions = submissionRepository.findAllByUserIdAndIsSolve(user.getId(), true);
		return submissions.size();
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
