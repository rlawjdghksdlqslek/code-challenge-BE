package goorm.code_challenge.ide.utils.save;

import static goorm.code_challenge.ide.utils.DockerCommand.*;
import static goorm.code_challenge.ide.utils.Template.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.UUID;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.ide.dto.reponse.CodePathDto;
import goorm.code_challenge.ide.utils.save.SaveUtil;

public class JavaScriptSave implements SaveUtil {
	@Override
	public CodePathDto saveCode(String code, List<TestCase> testCases) {
		final String uniqueDirName = UUID.randomUUID().toString();
		final File directory = new File("/tmp/" + uniqueDirName);
		validateExist(directory);
		boolean isSolved=false;
		//코드 받아서 파일로 생성
		try {
			createFile(directory, code, testCases.size());

			isSolved = runTestCases( testCases, directory);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.BAD_REQUEST,"잘못된 요청");
		}

		return new CodePathDto(isSolved,directory.getAbsolutePath()+"/script.js");
	}
	private void validateExist(File directory) {
		if (!directory.mkdirs()) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다");
		}
	}

	public ProcessBuilder startDockerRun(File tempDir) {
		return new ProcessBuilder(javaScriptCommand(tempDir)).redirectErrorStream(true);
	}

	public void createFile(File tempDir,String code,int size) {
		final File sourceFile = new File(tempDir, "script.js");
		final String codeWithLoop = String.format(JAVASCRIPT_TEMPLATE, size, code);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
			writer.write(codeWithLoop);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}


	private boolean runTestCases(List<TestCase> testCases,File tempDir) throws IOException {
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
			if(output.contains("Error")){
				return false;
			}
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
}
