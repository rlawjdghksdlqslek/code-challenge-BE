package goorm.code_challenge.user.dto.response;

import java.util.List;

import goorm.code_challenge.user.domain.User;
import lombok.Getter;

@Getter
public class MyPageResponse {
	private String userLoginId;
	private String userName;
	private String profileImage;
	private List<MyCodes> myCodes;

	public MyPageResponse(User user,List<MyCodes> myCodes) {
		this.userLoginId=user.getLoginId();
		this.userName=user.getName();
		this.profileImage=user.getProfileImage();
		this.myCodes=myCodes;
	}
}
