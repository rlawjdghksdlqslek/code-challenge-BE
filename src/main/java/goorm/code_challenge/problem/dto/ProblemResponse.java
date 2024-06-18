package goorm.code_challenge.problem.dto;

import java.util.ArrayList;
import java.util.List;

import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.problem.domain.Problem;
import lombok.Getter;

@Getter
public class ProblemResponse {
	private Long id;

	private String title;

	private String context;

	private String input;

	private String output;

	private String rank;

	private List<TestCaseResponse> testCases = new ArrayList<>();

	private String image;

	public ProblemResponse(Problem problem) {
		this.id = problem.getId();
		this.title = problem.getTitle();
		this.context = problem.getContext();
		this.input = problem.getInput();
		this.output = problem.getOutput();
		this.testCases = getSampleTestCases(problem);
		this.rank = problem.getRank();
		this.image = problem.getImage();
	}

	private List<TestCaseResponse> getSampleTestCases(Problem problem) {
		List<TestCaseResponse> sample = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			sample.add(new TestCaseResponse(problem.getTestCase().get(i)));
		}
		return sample;
	}

}
