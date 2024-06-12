package goorm.code_challenge.ide.utils;

import static goorm.code_challenge.ide.utils.DockerCommand.*;

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
import java.util.List;
import java.util.UUID;

import goorm.code_challenge.global.exception.CustomException;
import goorm.code_challenge.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaJudge implements JudgeUtil {
	@Override
	public void executeCode(String code) throws IOException, InterruptedException {
		//코드 받아서 파일로 생성
		File file = createFile(code);
		//파일 경로 확인
		System.out.println(file.getAbsolutePath());

		//컴파일 시작
		startCompile(file,file.getParentFile());

		delete(file.getParentFile());

	}

	@Override
	public ProcessBuilder startDockerRun(File tempDir) {
		System.out.println("실행 전");
		return new ProcessBuilder(javaCommand(tempDir)).redirectErrorStream(true);
	}

	public File createFile(String code) {
		String directoryPath = "/tmp";
		Path path = Paths.get(directoryPath);

		try {
			// 디렉토리가 존재하는지 확인
			if (!Files.exists(path)) {
				// 디렉토리가 존재하지 않으면 생성
				Files.createDirectory(path);
			}

			// 새로운 디렉토리 및 파일 경로 생성
			String newDirectoryName = UUID.randomUUID().toString();
			Path newDirectoryPath = Paths.get(directoryPath, newDirectoryName);
			Files.createDirectories(newDirectoryPath);
			Path javaFilePath = newDirectoryPath.resolve("Main.java");

			// Main.java 파일 생성 및 내용 작성
			Files.write(javaFilePath, code.getBytes());
			// 생성된 파일 객체 반환
			return javaFilePath.toFile();

		} catch (IOException e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류");
		}
	}

	private void startCompile(
		File sourceFile, //원본 파일
		File tempDir	//tmp 경로
	) throws InterruptedException, IOException {
		final String tempDirPath = tempDir.getAbsolutePath();
		final Process compileProcess = startDockerCompile(sourceFile, tempDirPath).start();

		validateCompile(compileProcess);
		runTestCases(tempDir);
		Thread.currentThread().interrupt();
	}

	private void runTestCases(File tempDir) throws IOException {
		final ProcessBuilder builder = startDockerRun(tempDir);
		final Process process = builder.start();
		final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String outputLine;
		while ((outputLine = reader.readLine()) != null) {
			// 실행 결과를 출력합니다.
			System.out.println(outputLine);
		}
	}

	private ProcessBuilder startDockerCompile(File sourceFile, String tempDirPath) {
		return new ProcessBuilder(compileCommand(sourceFile, tempDirPath));
	}

	private File createFile(File tempDir, String code) throws IOException {
		final File sourceFile = new File(tempDir, "Main.java");
		//final String codeWithLoop = String.format(JAVA_TEMPLATE, size, code);

		try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
			writer.write(code);
		} catch (IOException e) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다");
		}

		return sourceFile;
	}

	private void validateCompile(Process compileProcess) throws InterruptedException {
		if (compileProcess.waitFor() != 0) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "서버 오류 입니다");
		}
	}

	// private static void validateTimeOut(long currentTime, long startTime) {
	// 	if (currentTime - startTime > EXECUTION_TIME_LIMIT) {
	// 		throw new BadRequestException(ErrorCode.FAIL_PROCESSING_TIME_EXCEEDED);
	// 	}
	// }
	//
	// private static void validateJudge(String testCase, String output) {
	// 	if (!output.equals(testCase)) {
	// 		throw new BadRequestException(ErrorCode.FAIL_TESTCASE_NOT_PASSED);
	// 	}
	// }
	//
	private void validateExist(File tempDir) {
		if (!tempDir.mkdirs()) {
			throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 요청입니다");
		}
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
