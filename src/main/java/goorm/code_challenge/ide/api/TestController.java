package goorm.code_challenge.ide.api;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.ide.application.JudgeService;
import goorm.code_challenge.ide.dto.CodeSubmission;
import goorm.code_challenge.ide.dto.WrongResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController extends BaseController {
	private final JudgeService judgeService;
	@PostMapping("/ide")
	public ApiResponse<WrongResponse> test(@RequestBody @Validated CodeSubmission dto,@AuthenticationPrincipal UserDetails userDetails) throws
		IOException, InterruptedException {

		Map<String, String> judge = judgeService.judge(dto, userDetails);
		if(!judge.isEmpty()) {
			WrongResponse wrongResponse = new WrongResponse(judge);
			return makeAPIResponse(wrongResponse);
		}
		WrongResponse wrongResponse = new WrongResponse(0);
		return makeAPIResponse(wrongResponse);
	}
}
