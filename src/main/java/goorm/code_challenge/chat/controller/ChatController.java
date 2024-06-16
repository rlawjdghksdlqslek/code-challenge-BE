package goorm.code_challenge.chat.controller;

import goorm.code_challenge.chat.dto.MessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate template;

    @MessageMapping("/message/{chatRoomId}")
    public void sendMessage(@DestinationVariable("chatRoomId") Long roomId, MessageDto messageDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName(); // 로그인한 사용자의 이름
            messageDto.setSender(username);
        }

        template.convertAndSend("/sub/" + roomId, messageDto);
    }
}
