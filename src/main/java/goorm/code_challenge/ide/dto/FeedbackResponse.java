package goorm.code_challenge.ide.dto;

import lombok.Getter;

@Getter
public class FeedbackResponse {
	private String userName;
	private String code;

	public FeedbackResponse(String userName, String code) {
		this.userName = userName;
		this.code = code;
	}
}
