package goorm.code_challenge.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import goorm.code_challenge.user.domain.User;

public interface UserRepository extends JpaRepository<User,Long> {
	Boolean existsByName(String name);
	Boolean existsByLoginId(String loginId);
	User findByLoginId(String loginId);
	List<User> findTop10ByOrderByExpPointsDesc();
	List<User> findAllByOrderByExpPointsDesc();

}
