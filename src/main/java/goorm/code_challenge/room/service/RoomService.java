package goorm.code_challenge.room.service;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public RoomDTO createRoom(CreateRoomRequest roomRequest) {
        // hostName으로 User 찾기
        User host = userRepository.findByLoginId(roomRequest.getHostName());
        if (host == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "해당 호스트를 찾을 수 없습니다.");
        }

        Room room = roomRequest.toEntity(host);
        room.setRoomStatus(RoomStatus.WAITING);
        Room createdRoom = roomRepository.save(room);
        createdRoom.addParticipant(host); // 방 생성 시 호스트를 자동으로 참가자로 추가

        // 응답을 위해 RoomDTO로 변환
        return new RoomDTO(createdRoom);
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));
        return room;
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
    public void deleteRoom(Long roomId) {
        Room existingRoom = getRoom(roomId);

        User currentUser = getCurrentUser();
        if (!existingRoom.getHost().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "방장만 방을 삭제할 수 있습니다.");
        }

        roomRepository.delete(existingRoom);
    }

    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Transactional
    public void addUserToRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));

        if (room.getParticipants().size() >= 8) {
            throw new RoomFullException("방이 가득 찼습니다.");
        }

        User currentUser = getCurrentUser();
        room.addParticipant(currentUser); // 상태 업데이트 포함
        roomRepository.save(room);
    }

    @Transactional
    public void removeUserFromRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));

        User currentUser = getCurrentUser();
        room.removeParticipant(currentUser); // 상태 업데이트 포함

        if (room.getParticipants().isEmpty()) {
            roomRepository.delete(room);
        } else {
            // 새로운 방장 선정: 가장 먼저 입장한 사람을 새로운 방장으로 설정
            if (room.getHost().equals(currentUser) && !room.getParticipants().isEmpty()) {
                User newHost = room.getParticipants().get(0).getUser();
                room.setHost(newHost);
            }
            roomRepository.save(room);
        }
    }

    @Transactional(readOnly = true)
    public List<String> getRoomParticipants(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException("해당 방을 찾을 수 없습니다."));

        return room.getParticipants().stream()
                .map(participant -> participant.getUser().getLoginId())
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
}
