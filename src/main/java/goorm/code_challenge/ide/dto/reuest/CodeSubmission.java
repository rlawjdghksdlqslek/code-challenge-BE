package goorm.code_challenge.ide.dto.reuest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CodeSubmission {
	@NotNull(message = "방 번호는 필수 입력값입니다")
	private Long roomId;
	@NotNull(message = "문제 번호는 필수 입력값입니다")
	private Long problemId;
	@NotBlank(message = "정답 코드는 필수 입력값입니다")
	private String code;
	@NotBlank(message = "언어는 필수 입력값입니다")
	private String compileLanguage;

}
