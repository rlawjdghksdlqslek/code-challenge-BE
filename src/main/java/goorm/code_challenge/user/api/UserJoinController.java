package goorm.code_challenge.user.api;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.user.application.UserJoinService;
import goorm.code_challenge.user.dto.request.UserJoinRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserJoinController {
	private UserJoinService service;


	@PostMapping("/join")
	public String join(@Validated @RequestBody UserJoinRequest userJoinRequest, Errors errors){
		if(errors.hasErrors()){
			for(FieldError error :errors.getFieldErrors()){
				System.out.println(error.getDefaultMessage());
			}
		}
		service.joinService(userJoinRequest);

		return "ok";
	}
}
