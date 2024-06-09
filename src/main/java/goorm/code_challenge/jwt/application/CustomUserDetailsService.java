package goorm.code_challenge.jwt.application;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.jwt.dto.CustomUserDetails;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {

		this.userRepository = userRepository;
	}


	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		System.out.println("loginId = " + loginId);

		//DB에서 조회
		User userData = userRepository.findByLoginId(loginId);

		if (userData == null) {
			throw new CustomException(ErrorCode.BAD_REQUEST,"없는 아이디 입니다");
			//UserDetails에 담아서 return하면 AutneticationManager가 검증 함
		}

		return new CustomUserDetails(userData);
	}
}
