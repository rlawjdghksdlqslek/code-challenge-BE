package goorm.code_challenge.global.exception;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.Getter;

public class CustomException extends RuntimeException {
	@Getter
	private ErrorCode errorCode;
	private String message;

	public CustomException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public CustomException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.message = message;
	}

	public String GetMessage() {
		if (StringUtils.hasLength(this.message)) {
			return this.message;
		}
		return errorCode.getMessage();
	}
}
