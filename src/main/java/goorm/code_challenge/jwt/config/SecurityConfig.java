package goorm.code_challenge.jwt.config;

import goorm.code_challenge.jwt.application.JWTUtil;
import goorm.code_challenge.jwt.filter.CustomLogoutFilter;
import goorm.code_challenge.jwt.filter.JWTFilter;
import goorm.code_challenge.jwt.filter.LoginFilter;
import goorm.code_challenge.jwt.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final AuthenticationConfiguration authenticationConfiguration;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JWTUtil jwtUtil;

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				.cors((cors) -> cors
						.configurationSource(new CorsConfigurationSource() {
							@Override
							public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
								CorsConfiguration configuration = new CorsConfiguration();
								configuration.setAllowedOrigins(Arrays.asList("https://localhost:3000", "https://localhost:3000/code-editor"));
								configuration.setAllowedMethods(Collections.singletonList("*"));
								configuration.setAllowCredentials(true);
								configuration.setAllowedHeaders(Collections.singletonList("*"));
								configuration.setMaxAge(3600L);
								configuration.setExposedHeaders(Arrays.asList("access", "Authorization"));
								return configuration;
							}
						}));

		http
				.csrf((csrf) -> csrf.disable());

		http
				.formLogin((formLogin) -> formLogin.disable());

		http
				.httpBasic((httpBasic) -> httpBasic.disable());

		http
				.authorizeHttpRequests((auth) -> auth
						.requestMatchers("/api/login", "/api/join", "/api/reissue", "/index.html", "/", "/chat/**").permitAll()
						.requestMatchers("/pub/**").authenticated()  // 메시지 전송 경로 인증 요구
						.anyRequest().authenticated());

		http
				.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
				.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), refreshTokenRepository, jwtUtil, "/api/login"), UsernamePasswordAuthenticationFilter.class)
				.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);

		http
				.sessionManagement((session) -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}
}
