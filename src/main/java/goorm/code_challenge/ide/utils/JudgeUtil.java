package goorm.code_challenge.ide.utils;

import java.io.File;
import java.io.IOException;

public interface JudgeUtil {
	void executeCode(String code) throws IOException, InterruptedException;

	ProcessBuilder startDockerRun(File tempDir);
}
