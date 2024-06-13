package goorm.code_challenge.ide.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import goorm.code_challenge.ide.domain.Problem;

public interface JudgeUtil {
	Map<String,String> executeCode(String code, Problem problem);

	ProcessBuilder startDockerRun(File tempDir);
}
