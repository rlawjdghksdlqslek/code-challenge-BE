package goorm.code_challenge.chat.repository;

import goorm.code_challenge.chat.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Room의 roomId를 기준으로 ChatMessage 삭제
    void deleteByRoom_RoomId(Long roomId);
}
