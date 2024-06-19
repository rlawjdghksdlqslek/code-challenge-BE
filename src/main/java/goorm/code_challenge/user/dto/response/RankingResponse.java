package goorm.code_challenge.user.dto.response;

import goorm.code_challenge.user.domain.User;
import lombok.Getter;

@Getter
public class RankingResponse {
	private Long userId;
	private String userName;
	private String profileImage;
	private Integer rank;
	private Integer userPoint;
	private Integer useLevel;
	private Integer solvedCount;

	public RankingResponse(User user,int rank,int solvedCount) {
		this.userId = user.getId();
		this.userName = user.getName();
		this.profileImage = user.getProfileImage();
		this.userPoint = user.getExpPoints();
		this.useLevel = user.getLevel();
		this.rank=rank;
		this.solvedCount=solvedCount;
	}
}
