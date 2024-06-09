package goorm.code_challenge.user.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.user.domain.User;

@RestController
public class TestAPI {
	@GetMapping("/login")
	public String testLogin(){
		return "로그인 접근가능";
	}
	@GetMapping("/test")
	public String test(@AuthenticationPrincipal UserDetails userDetails){
		userDetails.getPassword();

		return "Main Controller : "+userDetails.getPassword();
	}@GetMapping("/test2")
	public String test2(@AuthenticationPrincipal UserDetails userDetails){
		userDetails.getPassword();

		return "Main Controller : "+userDetails.getUsername();
	}
	@GetMapping("/admin")
	public String adminP() {

		return "admin Controller";
	}
}
