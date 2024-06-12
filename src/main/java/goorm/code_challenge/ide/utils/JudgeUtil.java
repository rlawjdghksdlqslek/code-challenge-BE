package goorm.code_challenge.ide.utils;

import java.io.File;
import java.io.IOException;

public interface JudgeUtil {
	void executeCode(String code) throws IOException;

	ProcessBuilder startDockerRun(File tempDir);
}
