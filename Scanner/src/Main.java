import java.util.List;

public class Main {
    public static void main(String[] args) {
        String testCode = "public class Test { " +
                "  public static boolean calc() {" +
                "    int x = 15;" +
                "    int y = 20;" +
                "    int z = x+y;" +
                "    while(z >= 0) {" +
                "      z = z - 1;" +
                "    }" +
                "    if(x + y == x + y + z) {" +
                "      return true;" +
                "    } else {" +
                "      return false;"+
                "    }"+
                "  }" +
                "  public static void main(String[] args) {" +
                "    boolean out = calc();" +
                "  }" +
                "}";

        System.out.println("--- Step 1: Lexical Analysis (Scanner) ---");
        Scanner scanner = new Scanner(testCode);
        List<Scanner.Token> tokens = scanner.scanTokens();

        System.out.println("Generated " + tokens.size() + " tokens.");
        TokenVisualizer.visualizeAndOpen(tokens, "tokens.html");

        System.out.println("\n--- Step 2: Syntax Analysis (Parser) ---");
        Parser parser = new Parser(tokens);
        ParseTreeNode parseTreeRoot = parser.parse();

        if (parseTreeRoot != null) {
            System.out.println("Parsing successful! Generating Parse Tree visualization...");
            ParseTreeVisualizer.visualizeAndOpen(parseTreeRoot, "parse_tree.html");
        } else {
            System.out.println("Parsing failed due to syntax errors.");
        }
    }
}

/**
public class Test {
          public static boolean calc() {
            int x = 15;
            int y = 20;
            int z = x+y;
            while(z >= 0) {
              z = z - 1;
            }
            if(x + y == x + y + z) {
              return true;
            } else {

                return false;

            }
          }
          public static void main(String[] args) {
            boolean out = calc();
          }
}
*/
/**
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String testCode = "public class Demo { public static int calculate(int n) { int total = 0; while(n>0) { if(n>2){ total = total + n * 2; } else { total = total + n; } n=n-1; } return total; } public static void main(String[] args) { int result = calculate(4);}}";

        Scanner scanner = new Scanner(testCode);
        List<Scanner.Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        ParseTreeNode parseTreeRoot = parser.parse();

        if (parseTreeRoot != null) {
            System.out.println("Parse successful! Generating parse tree visualization...");
            ParseTreeVisualizer.visualizeAndOpen(parseTreeRoot, "parse_tree_visualization.html");
        } else {
            System.out.println("Parsing failed.");
        }
    }
}


/**import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Test A: Valid Token Stream (from Iteration 1)
        String validCode1 = "class Test { public static void main() { x = 10; while (x > 0) { x = x - 1; } } }";

        // Test B: Additional Valid Token Stream (with if-else and strings)
        String validCode2 = "class App { public static void main() { result = 42; if (result >= 50) { msg = \"pass\"; } else { msg = \"fail\"; } } }";

        // Test C: Invalid Token Stream (missing semicolon after assignment)
        String invalidCode = "class Bad { public static void main() { x = 10 } }";

        runTest("Test A (Standard Valid)", validCode1, true);
        runTest("Test B (Alternative Valid)", validCode2, true);
        runTest("Test C (Invalid - Missing Semicolon)", invalidCode, false);
    }

    private static void runTest(String testName, String code, boolean expectedValid) {
        Scanner scanner = new Scanner(code);
        List<Scanner.Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        boolean result = parser.parse();

        System.out.println(testName + ":");
        System.out.println("  Expected: " + (expectedValid ? "ACCEPTED" : "REJECTED"));
        System.out.println("  Actual:   " + (result ? "ACCEPTED" : "REJECTED"));
        System.out.println("  Status:   " + (result == expectedValid ? "PASS ✓" : "FAIL ✗"));
        System.out.println();
    }
}


/**import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Specify the path to your source file
        Path filePath = Path.of("src/Scanner.java");

        try {
            // 1. Read the entire source file into a string
            String sourceCode = Files.readString(filePath);

            System.out.println("Successfully read " + sourceCode.length() + " characters from " + filePath.getFileName());
            System.out.println("----------------------------------------");

            // 2. Pass the source code string into your scanner
            Scanner scanner = new Scanner(sourceCode);
            List<Scanner.Token> tokens = scanner.scanTokens();
            TokenVisualizer.visualizeAndOpen(tokens, "token_visualization.html");

            // 3. Print out the resulting token stream
            for (Scanner.Token token : tokens) {
                System.out.println(token);
            }

            System.out.println("----------------------------------------");
            System.out.println("Total tokens recognized: " + tokens.size());

        } catch (IOException e) {
            System.err.println("Error reading the source file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Scanning error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} */


/**import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] testInputs = {
                "count 42 total2 17 next_value",
                "x",
                "counter",
                "counter2",
                "value_2",
                "123",
                "12345",
                "abc123",
                "x=10",
                "total=count+25",
                "result=(x+10)*2;",
                "x==10",
                "x!=20",
                "x<=100",
                "x>=0",
                "calculate(value1,value2);",
                "ratio=a/b-c*2;",
                "check:val>=50;",
                "if",
                "ifValue",
                "value_if",
                "while ",
                "whileLoop ",
                "return ",
                "returnValue",
                "if(x>=10)",
                "return x;",
                "class Scanner { }",
                "else return 0;",
                "message=\"hello\"",
                "message=\"hello world\"",
                "message=\"x>=10\"",
                "message=\"while is a keyword\"",
                "message=\"this is // not a comment\"",
                "count=42; // this is a comment",
                "msg=\"He said \\\"hello\\\"\";",
                "if (x >= 10) { // check value\n return \"ok\"; }"
        };

        for (String test : testInputs) {
            System.out.println("Input: \"" + test + "\"");
            Scanner scanner = new Scanner(test);
            try {
                List<Scanner.Token> tokens = scanner.scanTokens();
                for (Scanner.Token token : tokens) {
                    System.out.println("  " + token);
                }
            } catch (Exception e) {
                System.out.println("  Error: " + e.getMessage());
            }
            System.out.println();
        }


        String sourceCode = Files.readString(Path.of("Scanner.java"));
        Scanner scanner = new Scanner(sourceCode);
        List<Scanner.Token> tokens = scanner.scanTokens();
    }
}
*/
