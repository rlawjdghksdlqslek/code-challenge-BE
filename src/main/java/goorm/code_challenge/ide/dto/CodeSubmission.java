package goorm.code_challenge.ide.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CodeSubmission {
	@NotBlank(message = "정답 코드는 필수 입력값입니다")
	private String code;
	@NotBlank(message = "언어는 필수 입력값입니다")
	private String compileLanguage;
}
