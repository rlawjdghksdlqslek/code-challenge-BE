package goorm.code_challenge.user.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserJoinRequest {

	@NotBlank(message = "로그인 아이디는 필수 입니다")
	@Pattern(regexp = "^[A-Za-z\\d]+$", message = "로그인 아이디는 영어 또는 숫자만 가능합니다")
	private String loginId;

	@NotBlank(message = "비밀번호는 필수 입니다")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 8자 이상 영어와 숫자를 포함 해야합니다")
	private String password;

	@NotBlank(message = "닉네임은 필수 입니다")
	private String name;
}
