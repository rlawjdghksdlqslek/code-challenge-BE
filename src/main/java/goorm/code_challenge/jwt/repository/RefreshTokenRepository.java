package goorm.code_challenge.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import goorm.code_challenge.jwt.domain.RefreshToken;
import jakarta.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
	Boolean existsByRefresh(String refresh);
	@Transactional
	void deleteByRefresh(String refresh);
}
