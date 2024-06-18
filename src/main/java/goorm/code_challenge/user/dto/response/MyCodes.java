package goorm.code_challenge.user.dto.response;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.problem.domain.Problem;
import lombok.Getter;

@Getter
public class MyCodes {
	private Long roomId;
	private Long problemId;
	private String problemName;
	private LocalDateTime submitTime;
	private LocalTime durationTime;
	private boolean isSolved;

	public MyCodes(Submission submission, Problem problem) {
		this.roomId = submission.getRoomId();
		this.submitTime = submission.getSubmitTime();
		this.isSolved = submission.isSolve();
		this.durationTime = submission.getDurationTime();
		this.problemId=problem.getId();
		this.problemName=problem.getTitle();
	}
}
