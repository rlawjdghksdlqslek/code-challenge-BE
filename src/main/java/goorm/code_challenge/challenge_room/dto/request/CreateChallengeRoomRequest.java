package goorm.code_challenge.challenge_room.dto.request;

import goorm.code_challenge.challenge_room.domain.ChallengeRoom;
import goorm.code_challenge.challenge_room.domain.RoomDifficulty;
import goorm.code_challenge.challenge_room.domain.RoomStatus;
import goorm.code_challenge.user.domain.User;
import lombok.Getter;

@Getter
public class CreateChallengeRoomRequest {
    private String roomName;
    private RoomDifficulty roomDifficulty;
    private String description;
    private RoomStatus roomStatus;

    public CreateChallengeRoomRequest() {
    }

    public ChallengeRoom toEntity(User user) {
        return ChallengeRoom.builder().user(user).roomName(this.roomName).roomDifficulty(this.roomDifficulty).description(this.description).roomStatus(this.roomStatus).build();
    }

}
