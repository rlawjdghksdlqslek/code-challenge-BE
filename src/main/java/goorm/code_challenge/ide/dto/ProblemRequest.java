package goorm.code_challenge.ide.dto;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProblemRequest {
	@NotBlank(message = "제목은 필수 입니다")
	private String title;

	@NotBlank(message = "내용은 필수 입니다")
	private String context;

	@NotBlank(message = "난이도는 필수 입니다")
	private String rank;

	private String image;

	private Map<String,String> testCase;
}

