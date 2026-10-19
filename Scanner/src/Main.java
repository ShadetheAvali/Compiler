import java.io.IOException;
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
}


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
