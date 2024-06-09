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
		// 헤더에서 access키에 담긴 토큰을 꺼냄
		String accessToken = request.getHeader("access");

		// 토큰이 없다면 다음 필터로 넘김
		if (accessToken == null) {

			filterChain.doFilter(request, response);

			return;
		}

		// 토큰 만료 여부 확인, 만료시 다음 필터로 넘기지 않음
		try {
			jwtUtil.isExpired(accessToken);
		} catch (ExpiredJwtException e) {

			sendErrorResponse(response,ErrorCode.UNAUTHORIZED,"만료된 토큰 입니다");
			//response status code
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		/// 토큰이 access인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(accessToken);

		if (!category.equals("access")) {

			//response body
			sendErrorResponse(response,ErrorCode.UNAUTHORIZED,"유효하지 않은 토큰입니다");

			//response status code
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		//토큰에서 username과 role 획득
		String username = jwtUtil.getUsername(accessToken);
		String role = jwtUtil.getRole(accessToken);

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
