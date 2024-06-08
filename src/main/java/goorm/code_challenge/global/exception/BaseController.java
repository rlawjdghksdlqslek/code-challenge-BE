package goorm.code_challenge.global.exception;

import java.util.List;

import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;

abstract public class BaseController {
	@ExceptionHandler(CustomException.class)
	public <T> ApiResponse<T> customExceptionHandler(HttpServletResponse response, CustomException customException) {
		response.setStatus(customException.getErrorCode().getHttpStatus().value());

		return new ApiResponse<T>(
			customException.getErrorCode().getCode(),
			customException.GetMessage());
	}

	public <T> ApiResponse<T> makeAPIResponse(List<T> result) {
		return new ApiResponse<>(result);
	}

	public <T> ApiResponse<T> makeAPIResponse(T result) {
		return new ApiResponse<>(result);
	}
}
