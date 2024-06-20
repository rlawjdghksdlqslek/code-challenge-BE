package goorm.code_challenge.ide.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.application.JudgeService;
import goorm.code_challenge.ide.application.SaveService;
import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.ide.dto.reuest.CodeSubmission;
import goorm.code_challenge.ide.dto.reuest.FeedbackRequest;
import goorm.code_challenge.ide.dto.reponse.FeedbackResponse;
import goorm.code_challenge.ide.dto.reponse.WrongResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class IDEController extends BaseController {
	private final JudgeService judgeService;
	private final SaveService saveService;
	@PostMapping("/code/run")
	public ApiResponse<WrongResponse> codeRun(@RequestBody @Validated CodeSubmission dto, Errors errors){
		validationRequest(errors);

		Map<String, String> judge = judgeService.judge(dto);
		if(!judge.isEmpty()) {
			WrongResponse wrongResponse = new WrongResponse(judge);
			return makeAPIResponse(wrongResponse);
		}
		WrongResponse wrongResponse = new WrongResponse(0);
		return makeAPIResponse(wrongResponse);
	}
	@PostMapping("/code/submit")
	public ApiResponse<Submission> submit( @Validated @RequestBody CodeSubmission dto,@AuthenticationPrincipal UserDetails userDetails,Errors errors) {
		validationRequest(errors);

		Submission submission = saveService.saveCode(dto, userDetails);
		return makeAPIResponse(submission);
	}
	@GetMapping("/code/{roomId}")
	public ApiResponse<List<FeedbackResponse>> getCodes( @PathVariable("roomId") Long roomId,@RequestParam("problemId") Long problemId) {
		List<FeedbackResponse> code = saveService.getCodes(roomId,problemId);
		return makeAPIResponse(Collections.singletonList(code));
	}
	@GetMapping("/myCode/{roomId}")
	public ApiResponse<FeedbackResponse> getMyCode(@PathVariable("roomId") Long roomId,@RequestParam("problemId") Long problemId) {
		FeedbackResponse code = saveService.getMyCode(roomId,problemId);
		return makeAPIResponse(code);
	}
	public void validationRequest(Errors errors){
		if (errors.hasErrors()) {
			for (FieldError error : errors.getFieldErrors()) {
				throw new CustomException(ErrorCode.BAD_REQUEST, error.getDefaultMessage());
			}
		}
	}
}
