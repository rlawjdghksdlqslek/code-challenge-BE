package goorm.code_challenge.global.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
	private Status status;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<T> results;

	public ApiResponse(int code,String message){
		this.status=new Status(code,message);
	}
	public ApiResponse(List<T> results){
		this.status=new Status(ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
		this.results=results;
	}
	public ApiResponse(T results){
		this.status=new Status(ErrorCode.OK.getCode(), ErrorCode.OK.getMessage());
		this.results=List.of(results);
	}

	@Getter
	@AllArgsConstructor
	private static class Status {
		private int code;
		private String message;
	}
}
