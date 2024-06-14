package goorm.code_challenge.ide.dto;

import lombok.Getter;

@Getter
public class CodePathDto {
	private boolean isSolved;
	private String filePath;

	public CodePathDto(boolean isSolved, String filePath) {
		this.isSolved = isSolved;
		this.filePath = filePath;
	}
}
