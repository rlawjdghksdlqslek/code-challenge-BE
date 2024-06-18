package goorm.code_challenge.user.api;

import java.io.IOException;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.BaseController;
import goorm.code_challenge.user.application.MyPageService;
import goorm.code_challenge.user.dto.request.UserNameUpdateRequest;
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
	@PutMapping("/myInfo/name")
	public ApiResponse<String> updateMyName(@Validated @RequestBody UserNameUpdateRequest userNameUpdateRequest, Errors errors){
		myPageService.updateName(userNameUpdateRequest, errors);

		return makeAPIResponse("이름이 변경 되었습니다");
	}
	@PutMapping("/myInfo/image")
	public ApiResponse<String> updateMyImage( @RequestPart("image") MultipartFile multipartFile) throws IOException {
		myPageService.updateImage(multipartFile);

		return makeAPIResponse("이미지가 변경 되었습니다");
	}
}
