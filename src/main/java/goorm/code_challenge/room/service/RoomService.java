package goorm.code_challenge.room.service;

import goorm.code_challenge.room.domain.Participant;
import goorm.code_challenge.room.domain.ParticipantStatus;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.domain.RoomStatus;
import goorm.code_challenge.room.dto.request.CreateRoomRequest;
import goorm.code_challenge.room.dto.response.ParticipantInfo;
import goorm.code_challenge.room.dto.response.RoomDTO;
import goorm.code_challenge.room.repository.RoomRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.room.api.RoomFullException;
import goorm.code_challenge.room.api.RoomNotFoundException;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoomDTO createRoom(CreateRoomRequest roomRequest) {
        User currentUser = getCurrentUser();
        Room room = roomRequest.toEntity(currentUser);
        room.setRoomStatus(RoomStatus.WAITING);

        // 호스트를 READY 상태로 설정
        room.addParticipant(currentUser);
        room.updateParticipantStatus(currentUser, ParticipantStatus.READY);

        Room createdRoom = roomRepository.save(room);

        return new RoomDTO(createdRoom);
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));
    }

    @Transactional
    public Room updateRoom(Long roomId, Room updatedRoom) {
        Room existingRoom = getRoom(roomId);

        User currentUser = getCurrentUser();
        if (!existingRoom.getHost().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "방장만 방을 수정할 수 있습니다.");
        }

        existingRoom.setRoomTitle(updatedRoom.getRoomTitle());
        existingRoom.setDuration(updatedRoom.getDuration());
        existingRoom.setAverageDifficulty(updatedRoom.getAverageDifficulty());
        existingRoom.setDescription(updatedRoom.getDescription());
        existingRoom.setProblems(updatedRoom.getProblems());

        validateRoom(existingRoom);
        return roomRepository.save(existingRoom);
    }

    @Transactional
    public String deleteRoomAndNotifyParticipants(Long roomId) {
        Room existingRoom = getRoom(roomId);

        User currentUser = getCurrentUser();
        if (!existingRoom.getHost().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "방장만 방을 삭제할 수 있습니다.");
        }

        roomRepository.delete(existingRoom);
        // 데이터베이스에 즉시 반영
        roomRepository.flush();

        notifyParticipants(existingRoom, "방이 삭제되었습니다.");
        return "방이 삭제되었습니다.";
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public ParticipantInfo addUserToRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));

        // participants 컬렉션 초기화
        Hibernate.initialize(room.getParticipants());

        if (room.getParticipants().size() >= 8) {
            throw new RoomFullException("방이 가득 찼습니다.");
        }

        User currentUser = getCurrentUser();
        if (room.isParticipant(currentUser)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "이미 참가한 유저입니다.");
        }

        room.addParticipant(currentUser);
        updateRoomStatus(room);

        return new ParticipantInfo(currentUser.getLoginId(), ParticipantStatus.WAITING.name(), currentUser.getName(), currentUser.getProfileImage());
    }


    @Transactional
    public String removeUserFromRoomAndHandleIfHost(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();
        if (!room.isParticipant(currentUser)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "이미 퇴장한 유저입니다.");
        }

        room.removeParticipant(currentUser);

        // 방장이 떠날 경우 방 삭제
        if (currentUser.equals(room.getHost())) {
            // 참가자가 남아 있어도 방을 삭제
            roomRepository.delete(room);
            // 데이터베이스에 즉시 반영되도록 처리
            roomRepository.flush();
            notifyParticipants(room, "방이 삭제되었습니다.");
            return "방이 삭제되었습니다.";
        }

        roomRepository.save(room);
        updateRoomStatus(room);

        return "방을 퇴장했습니다.";
    }

    @Transactional(readOnly = true)
    public List<String> getRoomParticipants(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));

        return room.getParticipants().stream()
                .map(participant -> participant.getUser().getLoginId())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipantInfo> getRoomParticipantInfos(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));

        return room.getParticipants().stream()
                .map(participant -> {
                    User user = participant.getUser();
                    return new ParticipantInfo(
                            user.getLoginId(),
                            participant.getStatus().name(),
                            user.getName(),
                            user.getProfileImage()
                    );
                })
                .collect(Collectors.toList());
    }

    private void validateRoom(Room room) {
        if (room.getRoomTitle().isBlank() || room.getRoomTitle().length() > 100) {
            throw new ValidationException("방 제목은 필수이며, 최대 100자까지 가능합니다.");
        }

        if (room.getDuration() <= 0) {
            throw new ValidationException("문제 풀이 시간은 양수여야 합니다.");
        }

        if (room.getAverageDifficulty() == null) {
            throw new ValidationException("평균 난이도는 필수 항목입니다.");
        }

        if (room.getDescription() != null && room.getDescription().length() > 500) {
            throw new ValidationException("방 설명은 최대 500자까지 가능합니다.");
        }

        if (room.getProblems() == null || room.getProblems().isEmpty()) {
            throw new ValidationException("적어도 하나의 문제를 선택해야 합니다.");
        }
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User currentUser = userRepository.findByLoginId(currentUserName);

        if (currentUser == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "현재 사용자를 찾을 수 없습니다.");
        }

        return currentUser;
    }

    @Transactional
    public void updateParticipantStatus(Long roomId, Long userId, ParticipantStatus status) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "해당 방을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "해당 사용자를 찾을 수 없습니다."));

        room.updateParticipantStatus(user, status);
        roomRepository.save(room);
    }

    @Transactional
    public String startRoom(Long roomId, User currentUser) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "해당 방을 찾을 수 없습니다."));

        if (!room.getHost().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "방장만 방을 시작할 수 있습니다.");
        }

        if (room.getParticipants().size() < 2) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "방을 시작하기 위해 최소 2명(방장 포함)이 필요합니다.");
        }

        if (room.allParticipantsReady()) {
            room.setRoomStatus(RoomStatus.ONGOING);
            roomRepository.save(room);
            return "방이 시작되었습니다.";
        } else {
            return "모든 참가자가 준비되지 않았습니다.";
        }
    }

    private void updateRoomStatus(Room room) {
        if (room.getParticipants().size() < 8) {
            room.setRoomStatus(RoomStatus.WAITING);
        }
        roomRepository.save(room);
    }

    private void notifyParticipants(Room room, String message) {
        room.getParticipants().forEach(participant -> {
            sendNotification(participant.getUser(), message);
        });
    }

    private void sendNotification(User user, String message) {
        log.info("Notifying user {}: {}", user.getLoginId(), message);
    }
}
