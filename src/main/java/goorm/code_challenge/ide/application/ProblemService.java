package goorm.code_challenge.ide.application;

import java.util.List;

import org.springframework.stereotype.Service;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.Problem;
import goorm.code_challenge.ide.dto.ProblemRequest;
import goorm.code_challenge.ide.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProblemService {
	private final ProblemRepository problemRepository;
	public Problem createProblem(ProblemRequest problemRequest) {
		List<String> input = problemRequest.getInput();
		List<String> output = problemRequest.getOutput();
		if(input.isEmpty()||output.isEmpty()||input.size()!=output.size()){
			throw new CustomException(ErrorCode.BAD_REQUEST,"테스트 케이스 형식이 잘못 됐습니다");
		}

		Problem problem = new Problem();
		problem.setTitle(problemRequest.getTitle());
		problem.setContext(problemRequest.getContext());
		problem.setImage(problemRequest.getImage());
		problem.setRank(problem.getRank());
		problem.setInput(problemRequest.getInput());
		problem.setOutput(problemRequest.getOutput());
		problemRepository.save(problem);
		return problem;
	}
}
