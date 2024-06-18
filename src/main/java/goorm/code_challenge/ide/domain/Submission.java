package goorm.code_challenge.ide.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Submission {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long roomId;

	private Long problemId;

	private Long userId;

	private String codePath;

	private boolean isSolve;

	private LocalTime durationTime;

	private LocalDateTime submitTime;
}
