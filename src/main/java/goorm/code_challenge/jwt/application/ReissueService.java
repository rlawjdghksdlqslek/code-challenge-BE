package goorm.code_challenge.jwt.application;

import java.util.Date;

import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.jwt.domain.RefreshToken;
import goorm.code_challenge.jwt.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReissueService {
	private final JWTUtil jwtUtil;
	private final RefreshTokenRepository refreshTokenRepository;

	public String createNewAccessToken(HttpServletRequest request, HttpServletResponse response) {
		String refresh = null;
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {

			if (cookie.getName().equals("refresh")) {

				refresh = cookie.getValue();
			}
		}

		if (refresh == null) {

			//response status code
			throw new CustomException(ErrorCode.BAD_REQUEST, "refresh 토큰이 비어 있습니다");
		}

		//expired check
		try {
			jwtUtil.isExpired(refresh);
		} catch (ExpiredJwtException e) {

			//response status code
			throw new CustomException(ErrorCode.BAD_REQUEST, "refresh 토큰이 만료 되었습니다");
		}

		// 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
		String category = jwtUtil.getCategory(refresh);

		if (!category.equals("refresh")) {

			//response status code
			throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 토큰입니다");
		}
		//DB에 저장되어 있는지 확인
		Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
		if (!isExist) {

			//response body
			throw new CustomException(ErrorCode.BAD_REQUEST, "유효하지 않은 토큰입니다");
		}

		String username = jwtUtil.getUsername(refresh);
		String role = jwtUtil.getRole(refresh);

		//make new JWT
		String newRefresh = jwtUtil.createJwt("refresh", username, role, 24 * 60 * 60 * 1000L);
		refreshTokenRepository.deleteByRefresh(refresh);
		addRefreshEntity(username,newRefresh,24 * 60 * 60 * 1000L);
		response.addCookie(createCookie("refresh", newRefresh));

		return jwtUtil.createJwt("access", username, role, 30 * 60 * 1000L);

	}
	private void addRefreshEntity(String username, String refresh, Long expiredMs) {

		Date date = new Date(System.currentTimeMillis() + expiredMs);

		RefreshToken refreshEntity = new RefreshToken();
		refreshEntity.setLoginId(username);
		refreshEntity.setRefresh(refresh);
		refreshEntity.setExpiration(date.toString());

		refreshTokenRepository.save(refreshEntity);
	}

	private Cookie createCookie(String key, String value) {

		Cookie cookie = new Cookie(key, value);
		cookie.setMaxAge(24 * 60 * 60);
		//cookie.setSecure(true);
		//cookie.setPath("/");
		cookie.setHttpOnly(true);

		return cookie;
	}

}
