package goorm.code_challenge.room.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ParticipantInfo {
    private String loginId;
    private String status;

    public ParticipantInfo(String loginId, String status) {
        this.loginId = loginId;
        this.status = status;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
