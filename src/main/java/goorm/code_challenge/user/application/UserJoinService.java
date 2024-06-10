package goorm.code_challenge.user.application;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.user.domain.User;
import goorm.code_challenge.user.dto.request.UserJoinRequest;
import goorm.code_challenge.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserJoinService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	public User joinService(UserJoinRequest userJoinRequest) {
		String name = userJoinRequest.getName();
		String loginId = userJoinRequest.getLoginId();
		String password = userJoinRequest.getPassword();

		if (userRepository.existsByName(name)) {
			throw new CustomException(ErrorCode.CONFLICT, "존재하는 이름 입니다");
		}
		if (userRepository.existsByLoginId(loginId)) {
			//여기서 예외처리
			throw new CustomException(ErrorCode.CONFLICT, "존재하는 ID 입니다");
		}
		User joinUser = new User();
		joinUser.setRole("ROLE_ADMIN");
		joinUser.setLoginId(loginId);
		joinUser.setPassword(bCryptPasswordEncoder.encode(password));
		joinUser.setName(name);
		userRepository.save(joinUser);
		return joinUser;
	}
}
