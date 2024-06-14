package goorm.code_challenge.global.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileToString {
	public String changeFile(String filePath){
		Path path = Paths.get(filePath);

		try {
			// 파일의 모든 내용을 문자열로 읽어옴
			return Files.readString(path);
		} catch (IOException e) {
			// 파일을 읽는 도중 오류 발생 시 예외 처리
			System.err.println("파일을 읽는 도중 오류가 발생했습니다: " + e.getMessage());
			return null; // 예외 발생 시 null 반환 혹은 예외 처리 방법에 따라 다름
		}
	}
}
