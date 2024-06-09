package goorm.code_challenge.jwt.filter;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import goorm.code_challenge.global.exception.ApiResponse;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.jwt.application.JWTUtil;
import goorm.code_challenge.jwt.dto.CustomUserDetails;
import goorm.code_challenge.user.domain.User;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
	private final JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {
		String authorization = request.getHeader("Authorization");

		//Authorization 헤더 검증
		if (authorization == null || !authorization.startsWith("Bearer ")) {

			log.error("token null");
			filterChain.doFilter(request, response);
			//sendErrorResponse(response,ErrorCode.BAD_REQUEST,"잘못된 형식 입니다");

			//조건이 해당되면 메소드 종료 (필수)
			return;
		}
		log.error("authorization now");
		//Bearer 부분 제거 후 순수 토큰만 획득
		String token = authorization.split(" ")[1];

		//토큰 소멸 시간 검증
		try {
			jwtUtil.isExpired(token);
		} catch (ExpiredJwtException e) {

			sendErrorResponse(response,ErrorCode.UNAUTHORIZED,"만료된 토큰 입니다");

			filterChain.doFilter(request, response);
			return;
		}

		//토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(token);
		String role = jwtUtil.getRole(token);

		//userEntity를 생성하여 값 set
		User userEntity = new User();
		userEntity.setLoginId(username);
		userEntity.setPassword("temppassword");
		userEntity.setName("임시 유저");
		userEntity.setRole(role);

		//UserDetails에 회원 정보 객체 담기
		CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

		//스프링 시큐리티 인증 토큰 생성
		Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
			customUserDetails.getAuthorities());
		//세션에 사용자 등록
		SecurityContextHolder.getContext().setAuthentication(authToken);


		filterChain.doFilter(request, response);

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
