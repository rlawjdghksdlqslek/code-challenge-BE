package goorm.code_challenge.ide.utils;

public class Template {
	private Template() {
	}

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
	public static final String JAVASCRIPT_TEMPLATE = """
const { Worker, isMainThread } = require('worker_threads');
if (isMainThread) {
    const worker = new Worker(__filename);
    const readline = require('readline').createInterface({
        input: process.stdin,
    });
    let inputCount = 0;
    const getInput = () => {
        readline.question('Enter two numbers separated by space: ', (input) => {
            worker.postMessage(input);
            inputCount++;
            if (inputCount === 1) {
                setTimeout(() => {
                    worker.terminate().then(() => {
                        console.log('15 seconds');
                        process.exit();
                    });
                }, 15000);
            }
            if (inputCount < %d) {
                getInput();
            } else {
                readline.close();
            }
        });
    };
    getInput();
    worker.on('message', (result) => {
        console.log(result);
    });
    readline.on('close', () => {
    });
} else {
    const { parentPort } = require('worker_threads');
    parentPort.on('message', (input) => {
        const result = solution(input);
        parentPort.postMessage(result);
    });
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

