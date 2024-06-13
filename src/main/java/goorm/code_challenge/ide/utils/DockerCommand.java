package goorm.code_challenge.ide.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DockerCommand {

	//로컬 버전
	public static List<String> javaCommand(File directory) {
		//String hostDirectory = "/tmp";
		String containerDirectory = "/app";
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			"-v", "/tmp" + ":/app",
			"openjdk:17",
			"java", "-cp", "/app/" + directory.toString().replace("/tmp", ""), "Main"
		);
	}
	//배포 버전



	public static List<String> javaScriptCommand(File directory) {
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			"-v", "/home/ec2-user/IdeaProjects/ThinkTank_BE/tmp" + ":/app",
			"node:alpine",
			"node", "/app/" + directory.toString().replace("/tmp", "") + "/" + "script.js"
		);
	}

	public static List<String> pythonCommand(File directory) {
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			"-v", "/home/ec2-user/IdeaProjects/ThinkTank_BE/tmp" + ":/app",
			"python:3",
			"python", "/app/" + directory.toString().replace("/tmp", "") + "/" + "script.py"
		);
	}

	// 해당 디랙토리 컴파일
	// 로컬 버전
	public static List<String> compileCommand(File sourceFile, String tempDirPath) {
		return Arrays.asList(
			"docker", "run", "--rm",
			"-v", "/tmp" + "/" +
				tempDirPath.toString().replace("/tmp", "") +
				":/app",
			"-w", "/app",
			"openjdk:17",
			"javac", sourceFile.getName()
		);
	}


}
