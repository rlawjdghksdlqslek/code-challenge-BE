package goorm.code_challenge.ide.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.application.JudgeService;
import goorm.code_challenge.ide.dto.CodeSubmission;
import goorm.code_challenge.ide.dto.ProblemResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController extends BaseController {
	private final JudgeService judgeService;
	@PostMapping("/ide")
	public ApiResponse<ProblemResponse> test(@RequestBody @Validated CodeSubmission dto,@AuthenticationPrincipal UserDetails userDetails) throws
		IOException, InterruptedException {

		Map<String, String> judge = judgeService.judge(dto, userDetails);
		if(!judge.isEmpty()) {
			List<String> wrongInput = new ArrayList<>();
			List<String> wrongOutput= new ArrayList<>();
			for(Map.Entry<String, String> entry  : judge.entrySet()){
				wrongInput.add(entry.getKey());
				wrongOutput.add(entry.getValue());
			}
			ProblemResponse problemResponse = new ProblemResponse(wrongInput,wrongOutput);
			return makeAPIResponse(problemResponse);
		}
		ProblemResponse problemResponse = new ProblemResponse(0);
		return makeAPIResponse(problemResponse);
	}
}
