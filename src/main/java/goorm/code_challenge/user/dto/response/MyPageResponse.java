package goorm.code_challenge.user.dto.response;

import java.util.ArrayList;
import java.util.List;

import goorm.code_challenge.user.domain.User;
import lombok.Getter;

@Getter
public class MyPageResponse {
	private String userLoginId;
	private String userName;
	private String profileImage;
	private Integer level;
	private Float extraExp;
	private Integer ranking;
	private Integer totalExpPoint;
	private List<MyCodes> myCodes=new ArrayList<>();

	public MyPageResponse(User user,List<MyCodes> myCodes,Integer ranking) {
		this.userLoginId=user.getLoginId();
		this.userName=user.getName();
		this.profileImage=user.getProfileImage();
		this.level=user.getLevel();
		this.extraExp=user.getExtraExpPoints();
		this.totalExpPoint=user.getExpPoints();
		this.myCodes=myCodes;
		this.ranking=ranking;
	}
}
