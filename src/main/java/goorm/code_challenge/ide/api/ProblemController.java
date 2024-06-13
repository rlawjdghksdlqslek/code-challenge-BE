package goorm.code_challenge.ide.api;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.application.ProblemService;
import goorm.code_challenge.ide.domain.Problem;
import goorm.code_challenge.ide.dto.ProblemRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProblemController extends BaseController {
	private final ProblemService problemService;

	@PostMapping("/problem")
	public ApiResponse<Problem> create(
		@AuthenticationPrincipal UserDetails userDetails,
		@Validated @RequestBody ProblemRequest problemRequest, Errors errors){
		if (errors.hasErrors()) {
			for (FieldError error : errors.getFieldErrors()) {
				throw new CustomException(ErrorCode.BAD_REQUEST, error.getDefaultMessage());
			}
		}
		Problem problem = problemService.createProblem(problemRequest);
		return makeAPIResponse(problem);
	}

}
