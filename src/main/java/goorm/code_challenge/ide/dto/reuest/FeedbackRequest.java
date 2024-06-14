package goorm.code_challenge.ide.dto.reuest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class FeedbackRequest {
	@NotNull(message = "방 번호는 필수 입력값입니다")
	private Long roomId;
	@NotNull(message = "문제 번호는 필수 입력값입니다")
	private Long problemId;
	@NotNull(message = "작성자 번호는 필수 입력값입니다")
	private Long userId;
}
