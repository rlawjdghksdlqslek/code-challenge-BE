package goorm.code_challenge.challenge_room.controller;

import goorm.code_challenge.challenge_room.domain.ChallengeRoom;
import goorm.code_challenge.challenge_room.dto.request.CreateChallengeRoomRequest;
import goorm.code_challenge.challenge_room.service.ChallengeRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challengerooms")
public class ChallengeRoomController {
    private final ChallengeRoomService challengeRoomService;

    public ChallengeRoomController(ChallengeRoomService challengeRoomService) {
        this.challengeRoomService = challengeRoomService;
    }

    // 방 생성 엔드포인트
    @PostMapping
    public ResponseEntity<ChallengeRoom> createChallengeRoom(@RequestBody CreateChallengeRoomRequest request) {
        ChallengeRoom createdRoom = challengeRoomService.createChallengeRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    // 방 조회 엔드포인트
    @GetMapping
    public List<ChallengeRoom> getChallengeRooms() {
        return challengeRoomService.getChallengeRooms();
    }

    // 방 상세 조회 엔드포인트
    @GetMapping("/{roomId}")
    public ChallengeRoom getChallengeRoom(@PathVariable Long roomId) {
        return challengeRoomService.getChallengeRoomById(roomId);
    }

    // 방 업데이트 엔드포인트
    @PutMapping("/{roomId}")
    public ChallengeRoom updateChallengeRoom(@PathVariable Long roomId, @RequestBody CreateChallengeRoomRequest request) {
        return challengeRoomService.updateChallengeRoom(roomId, request);
    }

    // 방 삭제 엔드포인트
    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteChallengeRoom(@PathVariable Long roomId) {
        challengeRoomService.deleteChallengeRoom(roomId);
        return ResponseEntity.noContent().build();
    }
}