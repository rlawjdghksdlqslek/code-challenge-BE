package goorm.code_challenge.ide.dto.reponse;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;

@Getter
public class WrongResponse {
	private int wrongCount;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private Map<String,String> testCase;
	public WrongResponse(int wrongCont){
		this.wrongCount=wrongCont;
	}
	public WrongResponse(Map<String,String> testCase){
		this.wrongCount=testCase.size();
		this.testCase=testCase;
	}

}
