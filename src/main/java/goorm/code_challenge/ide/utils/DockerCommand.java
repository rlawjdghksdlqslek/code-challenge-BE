package goorm.code_challenge.ide.utils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DockerCommand {
	public static List<String> javaCommand(File directory) {
		return Arrays.asList(
			"docker", "run", "-i", "--rm",
			//"-v", "/Users/cheonseongjun/Desktop/code-challenge/build/tmp" + ":/app", //로컬 버전
			// //배포버전
			"openjdk:17",
			"java", "-cp", "/app/" + directory.toString().replace("/tmp", ""), "Main"
		);
	}

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

	public static List<String> compileCommand(File sourceFile, String tempDirPath) {
		return Arrays.asList(
			"docker", "run", "--rm",
			"-v", "/home/ec2-user/IdeaProjects/ThinkTank_BE/tmp" + "/" +
				tempDirPath.toString().replace("/tmp", "") +
				":/app",
			"-w", "/app",
			"openjdk:17",
			"javac", sourceFile.getName()
		);
	}
}
