package goorm.code_challenge.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private String sender;
    private String content;
    private String timestamp;
}
