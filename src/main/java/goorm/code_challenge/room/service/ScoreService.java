package goorm.code_challenge.room.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.ide.repository.SubmissionRepository;
import goorm.code_challenge.room.domain.Participant;
import goorm.code_challenge.room.domain.ParticipantStatus;
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
		Optional<Room> room = roomRepository.findById(roomId);
		if(room.isEmpty()){
			throw new CustomException(ErrorCode.BAD_REQUEST, "존재하지 않는 방입니다");
		}

		List<String> roomParticipants = roomService.getRoomParticipants(roomId);
		List<Submission> submissions = submissionRepository.findAllByRoomIdAndProblemId(roomId, problemId);
		if (roomParticipants.size() > submissions.size()) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "모든 참가자가 코드를 제출하지 않았습니다");
		}
		setAllParticipantsWait(room.get());
		List<Submission> sortedSubmissions = submissions.stream()
			.sorted(Comparator.comparing(Submission::getDurationTime))
			.toList();
		List<ScoreDTO> scoreDTOS = new ArrayList<>();
		int rankCount = 0;
		for (Submission submission : sortedSubmissions) {
			User user = userRepository.findById(submission.getUserId()).get();
			if (submission.isSolve()) {
				rankCount++;
				int exp = (roomParticipants.size() - rankCount) * 20;
				user.setExpPoints(user.getExpPoints() + exp);
				userRepository.save(user);
				scoreDTOS.add(new ScoreDTO(user.getName(), submission.getDurationTime(), submission.isSolve(), rankCount,
					user.getLevel(), user.getExtraExpPoints()));
			} else
				scoreDTOS.add(new ScoreDTO(user.getName(), submission.getDurationTime(), submission.isSolve(), 0,
					user.getLevel(), user.getExtraExpPoints()));
		}
		return scoreDTOS;

	}
	@Transactional
	public void setAllParticipantsWait(Room room){
		//System.out.println(room.getRoomId());
		List<Participant> participants = room.getParticipants();
		for (int i = 0; i < participants.size(); i++) {
			Long userId=participants.get(i).getUser().getId();
			roomService.updateParticipantStatus(room.getRoomId(), userId, ParticipantStatus.WAITING);
		}
	}

	public Boolean checkAllUserReady(Long roomId){
		Optional<Room> room = roomRepository.findById(roomId);
		if(room.isEmpty()){
			throw new CustomException(ErrorCode.BAD_REQUEST,"방을 찾을 수 없습니다");
		}
		return room.get().allParticipantsReady();
	}

}
