package goorm.code_challenge.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.user.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {
	Boolean existsByName(String name);
	Boolean existsByLoginId(String loginId);
	User findByLoginId(String loginId);

}
