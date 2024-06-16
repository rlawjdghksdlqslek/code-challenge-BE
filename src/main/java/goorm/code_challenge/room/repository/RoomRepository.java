package goorm.code_challenge.room.repository;

import goorm.code_challenge.room.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}