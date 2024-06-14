package goorm.code_challenge.codes.repository;

import goorm.code_challenge.codes.domain.CodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodesEntityRepository extends JpaRepository<CodeEntity, Long> {

    // Room의 roomId를 기준으로 CodeEntity 삭제
    void deleteByRoom_RoomId(Long roomId);
}
