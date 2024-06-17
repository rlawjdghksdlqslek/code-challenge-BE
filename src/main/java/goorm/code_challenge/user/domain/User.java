package goorm.code_challenge.user.domain;

import java.math.BigDecimal;

import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "USERS")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true)
	@NotBlank
	private String loginId;

	@NotBlank
	private String password;

	@Column(unique = true)
	@NotBlank
	private String name;

	private String refreshToken;

	private String profileImage;

	private int expPoints=0;

	private String role;

	public int getLevel(){
		return (this.expPoints/200+1);
	}
	public float getExtraExpPoints(){
		int extra=expPoints%200;
		BigDecimal extraDecimal = new BigDecimal(extra);
		BigDecimal divisor = new BigDecimal(200);
		BigDecimal result = extraDecimal.divide(divisor, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
		return result.floatValue();
	}

	public void passwordEncode(PasswordEncoder passwordEncoder) {
		this.password = passwordEncoder.encode(this.password);
	}

	public void updateRefreshToken(String updateRefreshToken) {
		this.refreshToken = updateRefreshToken;
	}

}
