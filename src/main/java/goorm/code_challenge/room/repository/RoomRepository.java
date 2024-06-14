package goorm.code_challenge.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import goorm.code_challenge.room.domain.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}