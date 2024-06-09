package goorm.code_challenge.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	OK(200, HttpStatus.OK,"OK"),
	BAD_REQUEST(400,HttpStatus.BAD_REQUEST,"잘못된 파라미터 요청입니다"),
	CONFLICT(409,HttpStatus.CONFLICT,"중복된 데이터 입니다"),
	INTERNAL_SERVER_ERROR(500,HttpStatus.INTERNAL_SERVER_ERROR,"시스템 오류"),
	UNAUTHORIZED(401,HttpStatus.UNAUTHORIZED,"로그인 실패");
	private final int code;
	private final HttpStatus httpStatus;
	private final String message;

	ErrorCode(int code, HttpStatus httpStatus, String message) {
		this.code = code;
		this.httpStatus = httpStatus;
		this.message = message;
	}
}
