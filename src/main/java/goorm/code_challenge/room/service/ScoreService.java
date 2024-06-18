package goorm.code_challenge.room.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.ide.repository.SubmissionRepository;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.dto.response.ParticipantInfo;
import goorm.code_challenge.room.dto.response.ScoreDTO;
import goorm.code_challenge.room.repository.RoomRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScoreService {
	private final SubmissionRepository submissionRepository;
	private final RoomRepository roomRepository;
	private final RoomService roomService;
	private final UserRepository userRepository;

	@Transactional
	public List<ScoreDTO> getRoundScore(Long roomId, Long problemId) {
		List<ParticipantInfo> roomParticipants = roomService.getRoomParticipants(roomId);
		List<Submission> submissions = submissionRepository.findAllByRoomIdAndProblemId(roomId, problemId);
		List<Submission> sortedSubmissions = submissions.stream()
			.sorted(Comparator.comparing(Submission::getSubmitTime))
			.toList();
		List<ScoreDTO> scoreDTOS = new ArrayList<>();
		if (roomParticipants.size() > submissions.size()) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "모든 참가자가 코드를 제출하지 않았습니다");
		}
		int rankCount = 0;
		for (Submission submission : sortedSubmissions) {
			User user = userRepository.findById(submission.getUserId()).get();
			if (submission.isSolve()) {
				rankCount++;
				int exp = (roomParticipants.size() - rankCount) * 20;
				user.setExpPoints(user.getExpPoints() + exp);
				userRepository.save(user);
				scoreDTOS.add(new ScoreDTO(user.getName(), submission.getSubmitTime(), submission.isSolve(), rankCount,
					user.getLevel(), user.getExtraExpPoints()));
			} else
				scoreDTOS.add(new ScoreDTO(user.getName(), submission.getSubmitTime(), submission.isSolve(), 0,
					user.getLevel(), user.getExtraExpPoints()));
		}
		return scoreDTOS;

	}

}
