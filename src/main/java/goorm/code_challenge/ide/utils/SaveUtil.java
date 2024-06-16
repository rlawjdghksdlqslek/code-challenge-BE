package goorm.code_challenge.ide.utils;

import java.util.List;

import goorm.code_challenge.ide.domain.TestCase;
import goorm.code_challenge.ide.dto.reponse.CodePathDto;

public interface SaveUtil {
	CodePathDto saveCode(String code, List<TestCase> testCases);
}
