package goorm.code_challenge.user.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MyPageController {
	@GetMapping("/myInfo")
	public void readMyInfo(){


		return;
	}
	@GetMapping("/myInfo/{userId}")
	public void readUserInfo(@PathVariable(name = "userId") Long userId){


		return;
	}
	@GetMapping("/admin")
	public String adminP() {

		return "admin Controller";
	}
}
