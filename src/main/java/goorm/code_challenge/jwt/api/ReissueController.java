package goorm.code_challenge.jwt.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.jwt.application.ReissueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReissueController extends BaseController {
	private final ReissueService reissueService;
	@PostMapping("/reissue")
	ApiResponse<String> reissue(HttpServletRequest request, HttpServletResponse response){
		String newAccess = reissueService.createNewAccessToken(request,response);
		//response
		response.setHeader("access", newAccess);
		return makeAPIResponse("토큰 재발급 완료");
	}
}
