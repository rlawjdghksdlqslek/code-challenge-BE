package goorm.code_challenge.user.dto.response;

import java.time.LocalDateTime;

import goorm.code_challenge.ide.domain.Submission;
import goorm.code_challenge.problem.domain.Problem;

public class MyCodes {
	private Long roomId;
	private Long problemId;
	private String problemName;
	private LocalDateTime submitTime;
	private boolean isSolved;

	public MyCodes(Submission submission, Problem problem) {
		this.roomId = submission.getRoomId();
		this.submitTime = submission.getSubmitTime();
		this.isSolved = submission.isSolve();
		this.problemId=problem.getId();
		this.problemName=problem.getTitle();
	}
}
