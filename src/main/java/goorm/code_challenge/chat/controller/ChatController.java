package goorm.code_challenge.chat.controller;

import goorm.code_challenge.chat.dto.MessageDto;
import goorm.code_challenge.chat.service.ChatMessageService;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.repository.RoomRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatMessageService chatMessageService;

    @MessageMapping("/message/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long roomId, MessageDto messageDto,Authentication authentication) {
        chatMessageService.chatService(roomId, messageDto,authentication);
    }
}
