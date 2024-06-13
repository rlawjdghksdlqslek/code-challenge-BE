package goorm.code_challenge.ide.utils;

import static goorm.code_challenge.ide.utils.DockerCommand.*;
import static goorm.code_challenge.ide.utils.Template.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.Problem;
import goorm.code_challenge.ide.dto.ProblemResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaJudge implements JudgeUtil {
	@Override
	public Map<String,String> executeCode(String code, Problem problem)  {
		final String uniqueDirName = UUID.randomUUID().toString();
		final File directory = new File("/tmp/" + uniqueDirName);
		validateExist(directory);
		Map<String,String> wrongCasesMap;
		//코드 받아서 파일로 생성
		try {
			final File sourceFile = createFile(directory, code, problem.getInput().size());

			wrongCasesMap = startCompile(sourceFile, problem, directory);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.BAD_REQUEST,"잘못된 요청");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR,"시간 초과");
		} finally {
			//delete(directory);
		}

		//delete(file.getParentFile());
		return wrongCasesMap;

	}

	private void validateExist(File directory) {
		if (!directory.mkdirs()) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다");
		}
	}

	@Override
	public ProcessBuilder startDockerRun(File tempDir) {
		return new ProcessBuilder(javaCommand(tempDir)).redirectErrorStream(true);
	}

	public File createFile(File tempDir,String code,int size) {
		final File sourceFile = new File(tempDir, "Main.java");
		final String codeWithLoop = String.format(JAVA_TEMPLATE, size, code);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
			writer.write(codeWithLoop);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return sourceFile;
	}

	private Map<String, String> startCompile(
		File sourceFile, //원본 파일 ex/tmp/UUID/파일이름.java
		Problem problem,
		File tempDir	//tmp 경로 ex/tmp/UUID

	) throws InterruptedException, IOException {
		final String tempDirPath = tempDir.getAbsolutePath(); //절대 경로 확인
		final Process compileProcess = startDockerCompile(sourceFile, tempDirPath).start(); //도커 컴파일 시작

		validateCompile(compileProcess);
		Map<String, String> wrongCasesMap = runTestCases(problem, tempDir);
		Thread.currentThread().interrupt();
		return wrongCasesMap;
	}

	private void validateCompile(Process compileProcess) throws InterruptedException {
		if (compileProcess.waitFor() != 0) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "컴파일 오류");
		}
	}

	private ProcessBuilder startDockerCompile(File sourceFile, String dirPath) {
		return new ProcessBuilder(compileCommand(sourceFile, dirPath));
	}

	private Map<String, String> runTestCases(Problem problem,File tempDir) throws IOException {
		final ProcessBuilder builder = startDockerRun(tempDir);
		final long startTime = System.currentTimeMillis();
		final Process process = builder.start();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		Map<String,String> wrongCasesMap = new HashMap<>();

		for (int i = 0; i < 10; i++) {
			writer.write(problem.getInput().get(i) + "\n");
			writer.flush();
			final String output = reader.readLine();
			final long currentTime = System.currentTimeMillis();

			validateTimeOut(currentTime, startTime);
			if(!validateJudge(problem.getOutput().get(i), output)){
				System.out.println(problem.getOutput().get(i)+" 출력확인--------"+output+" 실제 결광");
				wrongCasesMap.put(problem.getInput().get(i),problem.getOutput().get(i));
			}
		}
		return wrongCasesMap;
	}


	private static void validateTimeOut(long currentTime, long startTime) {
		if (currentTime - startTime > 15000) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR,"시간 초과");
		}
	}

	private static Boolean validateJudge(String testCase, String output) {
		if (!output.equals(testCase)) {
			return false;
		}
		return true;
	}


}
