package goorm.code_challenge.room.api;

import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.dto.response.RoomDTO;
import goorm.code_challenge.room.service.RoomService;
import goorm.code_challenge.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomDTO> createRoom(@Valid @RequestBody RoomDTO roomDTO) {
        // CreateRoomRequest -> User 객체를 가져와서 Room 엔티티로 변환
        User currentUser = roomService.getCurrentUser(); // User를 가져오는 로직 필요
        Room createdRoom = roomService.createRoom(roomDTO.toEntity(currentUser));
        return new ResponseEntity<>(new RoomDTO(createdRoom), HttpStatus.CREATED);  // HTTP 201 Created
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoom(@PathVariable("roomId") Long roomId) {
        Room room = roomService.getRoom(roomId);
        RoomDTO roomDTO = new RoomDTO(room);
        return new ResponseEntity<>(roomDTO, HttpStatus.OK);  // HTTP 200 OK
    }

    @PutMapping("/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable("roomId") Long roomId, @Valid @RequestBody RoomDTO updatedRoomDTO) {
        // RoomDTO -> Room 엔티티로 변환
        User currentUser = roomService.getCurrentUser(); // User를 가져오는 로직 필요
        Room updatedRoom = roomService.updateRoom(roomId, updatedRoomDTO.toEntity(currentUser));
        return new ResponseEntity<>(new RoomDTO(updatedRoom), HttpStatus.OK);  // HTTP 200 OK
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable("roomId") Long roomId) {
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>("방이 삭제되었습니다.", HttpStatus.OK);  // HTTP 200 OK
    }

    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();
        List<RoomDTO> roomDTOs = rooms.stream()
                .map(RoomDTO::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(roomDTOs, HttpStatus.OK);  // HTTP 200 OK
    }
}
