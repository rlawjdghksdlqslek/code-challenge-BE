package goorm.code_challenge.challenge_room.service;

import goorm.code_challenge.challenge_room.domain.ChallengeRoom;
import goorm.code_challenge.challenge_room.dto.request.CreateChallengeRoomRequest;
import goorm.code_challenge.challenge_room.repository.ChallengeRoomRepository;
import goorm.code_challenge.global.utils.CurrentUserGetter;
import goorm.code_challenge.user.domain.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChallengeRoomService {
    private final CurrentUserGetter currentUserGetter;
    private final ChallengeRoomRepository challengeRoomRepository;

    public ChallengeRoomService(final CurrentUserGetter currentUserGetter, final ChallengeRoomRepository challengeRoomRepository) {
        this.currentUserGetter = currentUserGetter;
        this.challengeRoomRepository = challengeRoomRepository;
    }

    // 방 생성
    @Transactional
    public ChallengeRoom createChallengeRoom(CreateChallengeRoomRequest request) {
        User user = currentUserGetter.getCurrentMember();
        ChallengeRoom challengeRoom = request.toEntity(user);
        return challengeRoomRepository.save(challengeRoom);
    }

    // 방 조회
    public List<ChallengeRoom> getChallengeRooms() {
        return challengeRoomRepository.findAll();
    }

    // 방 상세 조회
    public ChallengeRoom getChallengeRoomById(Long roomId) {
        return challengeRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("ChallengeRoom not found with id: " + roomId));
    }

    // 방 업데이트
    @Transactional
    public ChallengeRoom updateChallengeRoom(Long roomId, CreateChallengeRoomRequest request) {
        User user = currentUserGetter.getCurrentMember();
        ChallengeRoom existingRoom = getChallengeRoomById(roomId);

        // 방장 권한 체크
        if (!existingRoom.getUser().equals(user)) {
            throw new UnauthorizedException("방장 권한이 없습니다.");
        }

        existingRoom.setRoomName(request.getRoomName());
        existingRoom.setRoomDifficulty(request.getRoomDifficulty());
        existingRoom.setDescription(request.getDescription());
        existingRoom.setRoomStatus(request.getRoomStatus());

        return challengeRoomRepository.save(existingRoom);
    }

    public class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

    // 방 삭제
    @Transactional
    public void deleteChallengeRoom(Long roomId) {
        ChallengeRoom existingRoom = getChallengeRoomById(roomId);

        // 방장 권한 체크
        User user = currentUserGetter.getCurrentMember();
        if (!existingRoom.getUser().equals(user)) {
            throw new UnauthorizedException("방을 삭제할 권한이 없습니다.");
        }

        challengeRoomRepository.delete(existingRoom);
    }
}