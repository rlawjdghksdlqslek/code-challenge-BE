package goorm.code_challenge.room.service;

import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.domain.RoomStatus;
import goorm.code_challenge.room.repository.RoomRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import jakarta.validation.ValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public Room createRoom(Room room) {
        room.setRoomStatus(RoomStatus.WAITING);
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public Room getRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.BAD_REQUEST, "해당 방을 찾을 수 없습니다."));
    }

    @Transactional
    public Room updateRoom(Long roomId, Room updatedRoom) {
        Room existingRoom = getRoom(roomId);

        // 수정 권한 체크 (방장인지 확인)
        User currentUser = getCurrentUser();
        if (!existingRoom.getHost().getId().equals(currentUser.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "방장만 방을 수정할 수 있습니다.");
        }

        existingRoom.setRoomTitle(updatedRoom.getRoomTitle());
        existingRoom.setDuration(updatedRoom.getDuration());
        existingRoom.setAverageDifficulty(updatedRoom.getAverageDifficulty());
        existingRoom.setDescription(updatedRoom.getDescription());

        validateRoom(existingRoom);
        return roomRepository.save(existingRoom);
    }

    @Transactional
    public void deleteRoom(Long roomId) {
        Room existingRoom = getRoom(roomId);

        // 삭제 권한 체크 (방장인지 확인)
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
}
