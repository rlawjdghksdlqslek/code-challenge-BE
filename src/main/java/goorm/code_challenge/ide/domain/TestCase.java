package goorm.code_challenge.ide.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TestCase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(nullable = false)
	private Problem problem;
	@Column(name = "input", nullable = false, columnDefinition = "VARCHAR(255)")
	private String input;
	@Column(name = "output", nullable = false, columnDefinition = "VARCHAR(255)")
	private String output;
	public TestCase(Problem problem,String input,String output){
		this.problem=problem;
		this.input=input;
		this.output=output;
	}
	public TestCase() {
		// 기본 생성자 내부에 초기화 로직 추가 가능
	}
}
