package goorm.code_challenge.ide.api;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.ide.application.JudgeService;
import goorm.code_challenge.ide.dto.CodeSubmission;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TestController {
	private final JudgeService judgeService;
	@PostMapping("/ide")
	public String test(@RequestBody @Validated CodeSubmission dto,@AuthenticationPrincipal UserDetails userDetails) throws
		IOException, InterruptedException {

		judgeService.judge(dto,userDetails);
		return "자바 테스트";
	}
}
