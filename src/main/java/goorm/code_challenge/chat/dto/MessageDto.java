package goorm.code_challenge.chat.dto;

import goorm.code_challenge.user.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private String senderUrl;
    private String sender;
    private String content;
    private String timestamp;

    public void setUser(User user) {
        this.senderUrl = user.getProfileImage();
        this.sender = user.getName();
    }
}
