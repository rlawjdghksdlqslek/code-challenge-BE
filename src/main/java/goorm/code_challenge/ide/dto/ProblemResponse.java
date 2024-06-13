package goorm.code_challenge.ide.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import goorm.code_challenge.global.exception.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ProblemResponse {
	int wrongCount;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	List<String> wrongInput;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	List<String> wrongOutput;
	public ProblemResponse(int wrongCont){
		this.wrongCount=wrongCont;
	}
	public ProblemResponse(List<String> wrongInput,List<String> wrongOutput){
		this.wrongCount=wrongOutput.size();
		this.wrongInput=wrongInput;
		this.wrongOutput=wrongOutput;
	}

}
