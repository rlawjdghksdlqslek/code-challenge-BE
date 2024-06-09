package goorm.code_challenge.jwt.filter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.jwt.application.JWTUtil;
import goorm.code_challenge.jwt.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

	private final AuthenticationManager authenticationManager;
	private final JWTUtil jwtUtil;

	public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws
		AuthenticationException,
		CustomException {
		String username = null;
		String password = null;
		if (request.getContentType() == null || !request.getContentType().equals("application/json")) {
			try {
				sendErrorResponse(response, ErrorCode.BAD_REQUEST, "잘못된 형식입니다.");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> authRequest = mapper.readValue(messageBody, new TypeReference<Map<String, String>>() {
			});
			username = authRequest.get("loginId");
			password = authRequest.get("password");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		System.out.println("username = " + username);

		if (username == null || username.trim().isEmpty()) {
			try {
				sendErrorResponse(response, ErrorCode.BAD_REQUEST, "아이디를 입력해 주세요");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return null;
		}

		if (password == null || password.trim().isEmpty()) {
			try {
				sendErrorResponse(response, ErrorCode.BAD_REQUEST, "비밀번호를 입력해 주세요");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return null;
		}

		//스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);
		//token에 담은 검증을 위한 AuthenticationManager로 전달
		return authenticationManager.authenticate(authToken);
	}

	//로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) {

		//UserDetailsS
		CustomUserDetails customUserDetails = (CustomUserDetails)authentication.getPrincipal();

		String username = customUserDetails.getUsername();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
		GrantedAuthority auth = iterator.next();

		String role = auth.getAuthority();

		String token = jwtUtil.createJwt(username, role,30 * 60 * 1000L);
		response.addHeader("Authorization", "Bearer " + token);
	}

	//로그인 실패시 실행하는 메소드
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
		AuthenticationException failed) throws IOException {
		sendErrorResponse(response,ErrorCode.UNAUTHORIZED,failed.getLocalizedMessage());
	}

	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode, String message) throws
		IOException {
		ApiResponse<Object> apiResponse = new ApiResponse<>(errorCode.getCode(), message);
		response.setStatus(errorCode.getHttpStatus().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		ObjectMapper objectMapper = new ObjectMapper();
		String jsonResponse = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(jsonResponse);
	}
}
