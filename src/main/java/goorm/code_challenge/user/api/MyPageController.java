package goorm.code_challenge.user.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.user.application.MyPageService;
import goorm.code_challenge.user.dto.response.MyPageResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MyPageController extends BaseController {
	private final MyPageService myPageService;

	@GetMapping("/myInfo")
	public ApiResponse<MyPageResponse> readMyInfo(){
		MyPageResponse myPageResponse = myPageService.getMyInfo();
		return makeAPIResponse(myPageResponse);
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
