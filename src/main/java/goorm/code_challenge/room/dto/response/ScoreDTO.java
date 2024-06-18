package goorm.code_challenge.room.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreDTO {
	private String username;
	private int rank;
	private LocalDateTime localDateTime;
	private boolean isSolved;
	private int level;
	private float extraEXP;

	public ScoreDTO(String username, LocalDateTime localDateTime,boolean isSolved,int rank,int level,float extraEXP) {
		this.username = username;
		this.isSolved = isSolved;
		this.localDateTime = localDateTime;
		this.rank=rank;
		this.level=level;
		this.extraEXP=extraEXP;
	}
}
