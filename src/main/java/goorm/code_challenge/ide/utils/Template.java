package goorm.code_challenge.ide.utils;

public class Template {
	private Template() {
	}

	public static final String JAVA_TEMPLATE =
		"import java.io.*;\n" +
			"import java.util.*;\n" +
			"import java.util.stream.*;\n" +
			"import java.util.concurrent.*;\n" +

			"public class Main {\n" +
			"    public static void main(String[] args) throws IOException {\n" +
			"    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();\n" +
			"    executor.schedule(() -> System.exit(0), 15, TimeUnit.SECONDS);\n" +
			"    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));" +
			"        for(int zxc = 0; zxc < %d; zxc++) {\n" +
			"			 String qwer = br.readLine();   \n" +
			"            System.out.println(solution(qwer));\n" +
			"        }\n" +
			"    }\n" +
			"    %s\n" +
			"}\n";

}
