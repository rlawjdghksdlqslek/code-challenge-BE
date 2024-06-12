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
	@GetMapping("/test")
	public String test(@AuthenticationPrincipal UserDetails userDetails){


		return "Main Controller : "+userDetails.getUsername();
	}
	@RequestMapping("/")
	public String adminP() {

		return "okr";
	}
}
