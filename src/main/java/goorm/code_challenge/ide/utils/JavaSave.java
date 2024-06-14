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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.ide.dto.CodePathDto;

public class JavaSave {
	public CodePathDto saveCode(String code, List<TestCase> testCases)  {
		final String uniqueDirName = UUID.randomUUID().toString();
		final File directory = new File("/tmp/" + uniqueDirName);
		validateExist(directory);
		boolean isSolved=false;
		//코드 받아서 파일로 생성
		try {
			final File sourceFile = createFile(directory, code, testCases.size());

			isSolved = startCompile(sourceFile, testCases, directory);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.BAD_REQUEST,"잘못된 요청");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR,"시간 초과");
		}

		return new CodePathDto(isSolved,directory.getAbsolutePath()+"/Main.java");

	}

	private void validateExist(File directory) {
		if (!directory.mkdirs()) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다");
		}
	}

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

	private boolean startCompile(
		File sourceFile, //원본 파일 ex/tmp/UUID/파일이름.java
		List<TestCase> testCases,
		File tempDir	//tmp 경로 ex/tmp/UUID

	) throws InterruptedException, IOException {
		final String tempDirPath = tempDir.getAbsolutePath(); //절대 경로 확인
		final Process compileProcess = startDockerCompile(sourceFile, tempDirPath).start(); //도커 컴파일 시작
		boolean  isSolved=isValidateCompile(compileProcess)&&isRunTestCases(testCases, tempDir);
		Thread.currentThread().interrupt();
		return isSolved;
	}

	private boolean isValidateCompile(Process compileProcess) throws InterruptedException {
		if (compileProcess.waitFor() != 0) {
			return false;
		}
		return true;
	}

	private ProcessBuilder startDockerCompile(File sourceFile, String dirPath) {
		return new ProcessBuilder(compileCommand(sourceFile, dirPath));
	}

	private boolean isRunTestCases(List<TestCase> testCases,File tempDir) throws IOException {
		final ProcessBuilder builder = startDockerRun(tempDir);
		final long startTime = System.currentTimeMillis();
		final Process process = builder.start();
		final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		boolean isSolved=true;
		boolean isNotTimeOut=true;

		for(TestCase testCase  : testCases){
			writer.write(testCase.getInput() + "\n");
			writer.flush();
			final String output = reader.readLine();
			final long currentTime = System.currentTimeMillis();

			isNotTimeOut=isValidateTimeOut(currentTime, startTime);
			if(!validateJudge(testCase.getOutput(), output)){
				isSolved=false;
			}
		}
		return isSolved&&isNotTimeOut;
	}


	private static boolean isValidateTimeOut(long currentTime, long startTime) {
		if (currentTime - startTime > 15000) {
			return false;
		}
		return true;
	}

	private static boolean validateJudge(String testCase, String output) {
		if (!output.equals(testCase)) {
			return false;
		}
		return true;
	}

	private void delete(File directory) {
		final File[] allContents = directory.listFiles();

		if (allContents != null) {
			for (File file : allContents) {
				delete(file);
			}
		}

		directory.delete();
	}
}
