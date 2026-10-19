import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class TokenVisualizer {

    private static String getBorderColor(Scanner.TokenType type) {
        switch (type) {
            case CLASS: case IF: case ELSE: case WHILE: case RETURN:
            case PUBLIC: case PRIVATE: case STATIC: case VOID: case INT: case NEW:
                return "#2196F3"; // Keywords: Blue
            case IDENTIFIER:
                return "#F44336"; // Identifiers: Red
            case INTEGER:
                return "#4CAF50"; // Numeric Literals: Green
            case STRING:
                return "#9C27B0"; // String Literals: Purple
            case ASSIGN: case PLUS: case MINUS: case MULTIPLY: case DIVIDE:
            case EQUAL: case NOT_EQUAL: case LESS: case LESS_EQUAL:
            case GREATER: case GREATER_EQUAL:
                return "#FF9800"; // Operators: Orange
            default:
                return "#9E9E9E"; // Punctuation & Others: Gray
        }
    }

    public static void visualizeAndOpen(List<Scanner.Token> tokens, String outputFileName) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("<meta charset='UTF-8'>\n");
        html.append("<title>Scanner Token Visualization</title>\n");
        html.append("<style>\n");
        html.append("  body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f6f9; color: #333; padding: 30px; }\n");
        html.append("  h1 { text-align: center; color: #2c3e50; margin-bottom: 20px; }\n");
        html.append("  .legend { display: flex; justify-content: center; gap: 25px; margin-bottom: 30px; background: white; padding: 15px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); flex-wrap: wrap; }\n");
        html.append("  .legend-item { display: flex; align-items: center; gap: 8px; font-weight: 600; font-size: 14px; }\n");
        html.append("  .legend-box { width: 16px; height: 16px; border: 3px solid; border-radius: 4px; }\n");
        html.append("  .code-container { background: white; padding: 40px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.08); overflow-x: auto; line-height: 2.5; }\n");
        html.append("  .line { display: flex; align-items: center; flex-wrap: wrap; margin-bottom: 8px; }\n");
        html.append("  .token-box { display: inline-flex; flex-direction: column; align-items: center; border: 2px solid; border-radius: 6px; padding: 4px 10px; margin: 0 4px; background: #fafafa; font-family: monospace; box-shadow: 0 1px 3px rgba(0,0,0,0.05); }\n");
        html.append("  .token-type { font-size: 10px; font-weight: bold; color: #000; text-transform: uppercase; margin-bottom: 2px; letter-spacing: 0.5px; }\n");
        html.append("  .token-lexeme { font-size: 14px; font-weight: 600; color: #2c3e50; white-space: pre; }\n");
        html.append("  .space { width: 15px; display: inline-block; }\n");
        html.append("</style>\n</head>\n<body>\n");

        html.append("<h1>Scanner Token Visualization</h1>\n");

        // Legend
        html.append("<div class='legend'>\n");
        html.append("  <div class='legend-item'><div class='legend-box' style='border-color: #2196F3;'></div>Keywords</div>\n");
        html.append("  <div class='legend-item'><div class='legend-box' style='border-color: #F44336;'></div>Identifiers</div>\n");
        html.append("  <div class='legend-item'><div class='legend-box' style='border-color: #4CAF50;'></div>Numeric Literals</div>\n");
        html.append("  <div class='legend-item'><div class='legend-box' style='border-color: #9C27B0;'></div>String Literals</div>\n");
        html.append("  <div class='legend-item'><div class='legend-box' style='border-color: #FF9800;'></div>Operators</div>\n");
        html.append("  <div class='legend-item'><div class='legend-box' style='border-color: #9E9E9E;'></div>Punctuation</div>\n");
        html.append("</div>\n");

        html.append("<div class='code-container'>\n");
        html.append("<div class='line'>\n");

        for (Scanner.Token token : tokens) {
            if (token.type == Scanner.TokenType.NEWLINE) {
                html.append("</div>\n<div class='line'>\n");
            } else if (token.type == Scanner.TokenType.WHITESPACE) {
                int spaces = token.lexeme.length();
                for (int i = 0; i < spaces; i++) {
                    html.append("<span class='space'></span>");
                }
            } else if (token.type != Scanner.TokenType.EOF) {
                String color = getBorderColor(token.type);
                html.append(String.format("<div class='token-box' style='border-color: %s;'>\n", color));
                html.append(String.format("  <span class='token-type'>%s</span>\n", token.type.name()));
                String escapedLexeme = token.lexeme.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
                html.append(String.format("  <span class='token-lexeme'>%s</span>\n", escapedLexeme));
                html.append("</div>\n");
            }
        }

        html.append("</div>\n");
        html.append("</div>\n");
        html.append("</body>\n</html>\n");

        try {
            File file = new File(outputFileName);
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(html.toString());
            }
            // Automatically open in default browser
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(file.toURI());
                System.out.println("Visualization generated and opened successfully: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error generating visualization: " + e.getMessage());
        }
    }
}