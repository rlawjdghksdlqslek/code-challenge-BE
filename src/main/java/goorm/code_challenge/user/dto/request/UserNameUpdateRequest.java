package goorm.code_challenge.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserNameUpdateRequest {
	@NotBlank(message = "이름은 필수 입니다")
	private String userName;
}
