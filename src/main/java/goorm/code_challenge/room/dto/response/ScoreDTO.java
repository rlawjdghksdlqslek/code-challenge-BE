package goorm.code_challenge.room.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoreDTO {
	private String username;
	private int rank;
	private LocalTime durationTile;
	private boolean isSolved;
	private int level;
	private float extraEXP;

	public ScoreDTO(String username, LocalTime durationTile,boolean isSolved,int rank,int level,float extraEXP) {
		this.username = username;
		this.isSolved = isSolved;
		this.durationTile = durationTile;
		this.rank=rank;
		this.level=level;
		this.extraEXP=extraEXP;
	}
}
