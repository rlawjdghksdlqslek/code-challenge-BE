package goorm.code_challenge.room.dto.response;

import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.room.domain.RoomStatus;
import goorm.code_challenge.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomDTO {
    private Long id;
    private String roomTitle;
    private String hostName; // 호스트 이름
    private Double averageDifficulty;
    private String description;
    private RoomStatus roomStatus;
    private int duration;

    public RoomDTO() {}

    public RoomDTO(Room room) {
        this.id = room.getRoomId();
        this.roomTitle = room.getRoomTitle();
        this.hostName = room.getHost().getLoginId(); // Assuming 'loginId' is used as unique identifier
        this.averageDifficulty = room.getAverageDifficulty();
        this.description = room.getDescription();
        this.roomStatus = room.getRoomStatus();
        this.duration = room.getDuration();
    }

    public Room toEntity(User host) {
        return Room.builder()
                .roomTitle(this.roomTitle)
                .duration(this.duration)
                .averageDifficulty(this.averageDifficulty)
                .description(this.description)
                .host(host)
                .roomStatus(this.roomStatus)
                .build();
    }
}
