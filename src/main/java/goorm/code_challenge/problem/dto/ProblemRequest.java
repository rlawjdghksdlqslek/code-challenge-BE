package goorm.code_challenge.problem.dto;

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

	@NotBlank(message = "입력은 필수 입니다")
	private String input;

	@NotBlank(message = "출력은 필수 입니다")
	private String output;


	@NotBlank(message = "난이도는 필수 입니다")
	private String rank;

	private String image;

	private Map<String,String> testCase;
}

