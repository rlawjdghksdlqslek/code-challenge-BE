package goorm.code_challenge.user.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestAPI {
	@GetMapping("/login")
	public String testLogin(){
		return "로그인 접근가능";
	}
	@GetMapping("/test")
	public String test(){
		return "로그인 접근가능";
	}
}
