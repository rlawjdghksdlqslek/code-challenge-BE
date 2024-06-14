package goorm.code_challenge.ide.dto;

import goorm.code_challenge.ide.domain.Problem;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ProblemResponse {
	private final Long id;

	private final String title;

	private final String context;

	private final String rank;

	private final String image;
	public ProblemResponse(Problem problem) {
		this.id = problem.getId();
		this.title = problem.getTitle();
		this.context = problem.getContext();
		this.rank = problem.getRank();
		this.image = problem.getImage();
	}
}
