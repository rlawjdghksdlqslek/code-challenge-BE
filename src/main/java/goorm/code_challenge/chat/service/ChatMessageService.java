package goorm.code_challenge.chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import goorm.code_challenge.chat.dto.MessageDto;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.repository.RoomRepository;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChatMessageService {
	private final SimpMessagingTemplate template;
	private final RoomRepository roomRepository;
	private final UserRepository userRepository;
	public void chatService(Long roomId, MessageDto messageDto,Authentication authentication){

		User user = getCurrentUser(authentication);
		log.info("User 를 찾았습니다: " + user.getName());
		Optional<Room> roomOptional = roomRepository.findById(roomId);
		if (roomOptional.isEmpty()) {
			log.error("해당하는 방을 찾을 수 없습니다 ID: " + roomId);
			throw new RuntimeException("방이 존재 하지 않습니다");
		}
		Room room = roomOptional.get();
		boolean isParticipant = room.isParticipant(user);
		if (!isParticipant) {
			log.error("해당하는 유저는 유저ID: " + user.getName() + " 방에 참가하지 않았습니다 방ID: " + roomId);
			throw new RuntimeException("Unauthorized access to chat room");
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		messageDto.setTimestamp(LocalDateTime.now().format(formatter));
		messageDto.setUser(user);
		template.convertAndSend("/sub/" + roomId, messageDto);
	}
	private User getCurrentUser(Authentication authentication) {
		if(authentication==null){
			throw new CustomException(ErrorCode.UNAUTHORIZED, "인증된 사용자가 존재하지 않습니다");
		}
		String currentUserName = authentication.getName();
		User currentUser = userRepository.findByLoginId(currentUserName);

		if (currentUser == null) {
			throw new CustomException(ErrorCode.UNAUTHORIZED, "현재 사용자를 찾을 수 없습니다.");
		}
		return currentUser;
	}

}
