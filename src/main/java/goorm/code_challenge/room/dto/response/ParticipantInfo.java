package goorm.code_challenge.room.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParticipantInfo {
    private String loginId;
    private String status;
    private String name;
    private String profileImage;

    public ParticipantInfo(String loginId, String status, String name, String profileImage) {
        this.loginId = loginId;
        this.status = status;
        this.name = name;
        this.profileImage = profileImage;
    }
}
