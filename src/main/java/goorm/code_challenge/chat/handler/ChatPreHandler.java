package goorm.code_challenge.chat.handler;

import goorm.code_challenge.jwt.application.JWTUtil;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.repository.RoomRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatPreHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String token = accessor.getFirstNativeHeader("access");

        if (token != null) {
            // JWT 유효성 검사
            if (jwtUtil.isExpired(token)) {
                log.warn("Expired JWT token");
                return null;
            }

            String username = jwtUtil.getUsername(token);
            if (username == null || username.isEmpty()) {
                log.warn("Invalid JWT token");
                return null;
            }

            // 사용자 인증 설정
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("Authenticated user: " + username);
        }

        // 구독하려는 방 존재 여부 확인
        Long roomId = Optional.ofNullable(accessor.getDestination())
                .filter(dest -> dest.startsWith("/sub/"))
                .map(dest -> {
                    try {
                        Long id = Long.parseLong(dest.split("/")[2]);
                        log.info("Attempting to subscribe to room: " + id);
                        return id;
                    } catch (NumberFormatException e) {
                        log.error("Invalid room ID format: " + dest.split("/")[2]);
                        return null;
                    }
                })
                .orElse(null);

        if (roomId != null) {
            Optional<Room> roomOptional = roomRepository.findById(roomId);
            if (roomOptional.isEmpty()) {
                log.warn("Room not found: " + roomId);
                return null; // 존재하지 않는 방으로의 구독을 차단
            }
        }

        log.info("PreSend: " + message.toString());
        return message;
    }
}
