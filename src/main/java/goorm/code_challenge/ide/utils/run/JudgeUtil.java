package goorm.code_challenge.ide.utils.run;

import java.io.File;
import java.util.List;
import java.util.Map;

import goorm.code_challenge.ide.domain.TestCase;

public interface JudgeUtil {
	Map<String,String> executeCode(String code, List<TestCase> testCase);

	ProcessBuilder startDockerRun(File tempDir);
}
