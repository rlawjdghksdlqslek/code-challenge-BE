package goorm.code_challenge.chat.handler;

import goorm.code_challenge.jwt.application.JWTUtil;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import java.util.Objects;
import java.util.Optional;
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final RoomRepository roomRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand stompCommand = accessor != null ? accessor.getCommand() : null;
        if (stompCommand == null) {
            return message;
        }

        switch (stompCommand) {
            case CONNECT:
                handleConnect(accessor);
                break;
            case SUBSCRIBE:
                if (!handleSubscribe(accessor)) {
                    return null; // 존재하지 않는 방으로의 구독을 차단
                }
                break;
            case SEND:
                handleSend(accessor);
                break;
            default:
                break;
        }

        log.info("PreSend: " + message.toString());
        return message;
    }
    private void handleConnect(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("access");
        log.info("CONNECT - Token: " + token);

        if (token != null) {
            String username = jwtUtil.getUsername(token);
            if (username != null && !username.isEmpty()) {
                log.info("Authenticated user: " + username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                accessor.setUser(authentication);
                //SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("토큰에서 유저 아이디를 찾을 수 없습니다");
            }
        } else {
            log.warn("토큰이 비어있습니다");
        }
    }

    private boolean handleSubscribe(StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();
        if (destination != null && destination.startsWith("/sub/")) {
            try {
                Long roomId = Long.parseLong(destination.split("/")[2]);
                log.info("Attempting to subscribe to room: " + roomId);
                Optional<Room> roomOptional = roomRepository.findById(roomId);
                if (roomOptional.isEmpty()) {
                    log.warn("존재하지 않는 방입니다: " + roomId);
                    return false; // 존재하지 않는 방으로의 구독을 차단
                }
            } catch (NumberFormatException e) {
                log.error("유효하지 않는 방 ID 포멧: " + destination.split("/")[2]);
                return false;
            }
        }
        return true;
    }

    private void handleSend(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("access");
        log.info("SEND - Token: " + token);

        if (token != null) {
            String username = jwtUtil.getUsername(token);
            if (username != null && !username.isEmpty()) {
                log.info("Authenticated user: " + username);
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
                accessor.setUser(authentication);
                //SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                log.warn("토큰에서 유저 아이디를 찾을 수 없습니다");
            }
        } else {
            log.warn("토큰이 비어있습니다");
        }
    }

}
