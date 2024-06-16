package goorm.code_challenge.ide.utils;

public class Template {
	private Template() {
	}

	// public static final String JAVA_TEMPLATE =
	// 	"import java.io.*;\n" +
	// 		"import java.util.*;\n" +
	// 		"import java.util.stream.*;\n" +
	// 		"import java.util.concurrent.*;\n" +
	//
	// 		"public class Main {\n" +
	// 		"    public static void main(String[] args) throws IOException {\n" +
	// 		"    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();\n" +
	// 		"    executor.schedule(() -> System.exit(0), 15, TimeUnit.SECONDS);\n" +
	// 		"    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));" +
	// 		"        for(int zxc = 0; zxc < %d; zxc++) {\n" +
	// 		"			 String qwer = br.readLine();   \n" +
	// 		"            System.out.println(solution(qwer));\n" +
	// 		"        }\n" +
	// 		"    }\n" +
	// 		"    %s\n" +
	// 		"}\n";
	public static final String JAVA_TEMPLATE =
		"""
			import java.io.*;
			import java.util.*;
			import java.util.stream.*;
			import java.util.concurrent.*;
			public class Main {
			    public static void main(String[] args) throws IOException {
			        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
			        executor.schedule(() -> System.exit(0), 15, TimeUnit.SECONDS);
			        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			        for(int zxc = 0; zxc < %d; zxc++) {
			            String qwer = br.readLine();
			            System.out.println(solution(qwer));
			        }
			    }
			   
			    %s
			   
			}
			""";
	public static final String PYTHON_TEMPLATE =
		"""
		import sys
		import threading
		import math
		import itertools
		import functools
		import collections
		import heapq
		import bisect
		import array
		import re
		import random
		import time
		import datetime
				 
		def main():
			threading.Timer(15.0, lambda: sys.exit(0)).start()
			for _ in range(%d):
				qwer = input()
				print(solution(qwer))
				 
		%s
				 
		if __name__ == "__main__":
			main()
		""";

	}

