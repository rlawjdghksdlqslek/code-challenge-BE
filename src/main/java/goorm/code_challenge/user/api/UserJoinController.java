package goorm.code_challenge.user.api;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.user.application.UserJoinService;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.dto.request.UserJoinRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserJoinController extends BaseController {
	private final UserJoinService service;

	@PostMapping("/join")
	public ApiResponse<String> join(@Validated @RequestBody UserJoinRequest userJoinRequest,
		Errors errors) {
		if (errors.hasErrors()) {
			for (FieldError error : errors.getFieldErrors()) {
				throw new CustomException(ErrorCode.BAD_REQUEST, error.getDefaultMessage());
			}
		}
		User user = service.joinService(userJoinRequest);
		return makeAPIResponse("회원가입 성공!");
	}
}
