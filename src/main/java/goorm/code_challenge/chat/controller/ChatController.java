package goorm.code_challenge.chat.controller;

import goorm.code_challenge.chat.dto.MessageDto;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.repository.RoomRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate template;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @MessageMapping("/message/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long roomId, MessageDto messageDto) {
        log.info("Received message for room: " + roomId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userRepository.findByLoginId(username);

            if (user == null) {
                log.error("Authenticated user not found in the repository: " + username);
                throw new RuntimeException("User not found");
            }

            if (user != null) {
                log.info("User found: " + user.getName());
                messageDto.setSender(user.getName()); // 메시지의 발신자 설정
            }

            Optional<Room> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isEmpty()) {
                log.error("Room not found for ID: " + roomId);
                throw new RuntimeException("Room not found");
            }

            Room room = roomOptional.get();
            boolean isParticipant = room.isParticipant(user);

            if (!isParticipant) {
                log.error("Unauthorized access by user: " + username + " to room: " + roomId);
                throw new RuntimeException("Unauthorized access to chat room");
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            messageDto.setTimestamp(LocalDateTime.now().format(formatter));

            template.convertAndSend("/sub/" + roomId, messageDto);
        } else {
            log.error("User not authenticated for room: " + roomId);
            throw new RuntimeException("Unauthorized access to chat room");
        }
    }
}
