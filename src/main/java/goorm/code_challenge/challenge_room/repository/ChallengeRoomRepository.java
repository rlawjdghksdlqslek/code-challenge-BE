package goorm.code_challenge.challenge_room.repository;

import goorm.code_challenge.challenge_room.domain.ChallengeRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRoomRepository extends JpaRepository<ChallengeRoom, Long> {
    // 여기에 추가적인 메서드가 필요한 경우 정의할 수 있습니다.
}