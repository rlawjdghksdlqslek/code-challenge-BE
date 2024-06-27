package goorm.code_challenge.room.dto.response;

import goorm.code_challenge.room.domain.Room;
import goorm.code_challenge.user.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoomDTO {
    private Long id;
    private String roomTitle;
    private String hostName;
    private String profileImage;
    private Integer averageDifficulty;
    private String description;
    private int duration;
    private List<Long> problems;
    private String roomStatus;

    public RoomDTO() {}

    public RoomDTO(Room room) {
        this.id = room.getRoomId();
        this.roomTitle = room.getRoomTitle();
        this.hostName = room.getHost().getName();
        this.profileImage = room.getHost().getProfileImage();
        this.averageDifficulty = room.getAverageDifficulty();
        this.description = room.getDescription();
        this.duration = room.getDuration();
        this.problems = room.getProblems();
        this.roomStatus = room.getRoomStatus().name();
    }

    public Room toEntity(User host) {
        return Room.builder()
                .roomTitle(this.roomTitle)
                .duration(this.duration)
                .averageDifficulty(this.averageDifficulty)
                .description(this.description)
                .host(host)
                .problems(this.problems)
                .build();
    }
}
