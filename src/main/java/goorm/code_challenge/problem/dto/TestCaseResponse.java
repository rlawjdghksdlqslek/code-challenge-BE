package goorm.code_challenge.problem.dto;

import goorm.code_challenge.ide.domain.TestCase;
import lombok.Getter;

@Getter
public class TestCaseResponse {
	private final String input;
	private final String output;

	public TestCaseResponse(TestCase testCase) {
		this.input = testCase.getInput();
		this.output = testCase.getOutput();
	}
}
