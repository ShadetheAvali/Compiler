import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    public enum TokenType {
        // Literals & Identifiers
        IDENTIFIER, INTEGER, STRING, CHAR_LITERAL, BOOLEAN_LITERAL, NULL_LITERAL,

        // Keywords
        CLASS, IF, ELSE, WHILE, RETURN, PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, VOID, INT, BOOLEAN, CHAR, PACKAGE, IMPORT, ENUM, NEW,

        // Operators & Punctuation
        ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE,
        EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,
        LEFT_PAREN, RIGHT_PAREN, COMMA, SEMICOLON, COLON, LEFT_BRACKET, RIGHT_BRACKET, LEFT_BRACE, RIGHT_BRACE, DOT, AT,
        INCREMENT, DECREMENT, PLUS_ASSIGN, MINUS_ASSIGN, TERNARY, DO, SWITCH, CASE, DEFAULT, BREAK, TRY, CATCH, THROWS, THROW,
        AND, OR, NEWLINE, WHITESPACE,

        EOF
    }

    public static class Token {
        public final TokenType type;
        public final String lexeme;

        public Token(TokenType type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }

        @Override
        public String toString() {
            return type + " " + lexeme;
        }
    }

    private final String input;
    private int position = 0;
    private static final Map<String, TokenType> keywords = new HashMap<>();

    static {
        keywords.put("class", TokenType.CLASS);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("return", TokenType.RETURN);
        keywords.put("public", TokenType.PUBLIC);
        keywords.put("private", TokenType.PRIVATE);
        keywords.put("protected", TokenType.PROTECTED);
        keywords.put("static", TokenType.STATIC);
        keywords.put("final", TokenType.FINAL);
        keywords.put("void", TokenType.VOID);
        keywords.put("int", TokenType.INT);
        keywords.put("boolean", TokenType.BOOLEAN);
        keywords.put("char", TokenType.CHAR);
        keywords.put("package", TokenType.PACKAGE);
        keywords.put("import", TokenType.IMPORT);
        keywords.put("enum", TokenType.ENUM);
        keywords.put("new", TokenType.NEW);
        keywords.put("do", TokenType.DO);
        keywords.put("switch", TokenType.SWITCH);
        keywords.put("case", TokenType.CASE);
        keywords.put("default", TokenType.DEFAULT);
        keywords.put("break", TokenType.BREAK);
        keywords.put("try", TokenType.TRY);
        keywords.put("catch", TokenType.CATCH);
        keywords.put("throws", TokenType.THROWS);
        keywords.put("throw", TokenType.THROW);
        keywords.put("true", TokenType.BOOLEAN_LITERAL);
        keywords.put("false", TokenType.BOOLEAN_LITERAL);
        keywords.put("null", TokenType.NULL_LITERAL);
    }

    public Scanner(String input) {
        this.input = input;
    }

    public Token nextToken() {
        while (position < input.length()) {
            char current = input.charAt(position);

            // 1. Handle Newlines and Whitespace for Visualization
            if (current == '\n') {
                position++;
                return new Token(TokenType.NEWLINE, "\n");
            }
            if (Character.isWhitespace(current)) {
                int start = position;
                while (position < input.length() && Character.isWhitespace(input.charAt(position)) && input.charAt(position) != '\n') {
                    position++;
                }
                return new Token(TokenType.WHITESPACE, input.substring(start, position));
            }

            // 2. Recognize Comments - Non-emitting
            if (current == '/' && position + 1 < input.length() && input.charAt(position + 1) == '/') {
                position += 2; // Skip past '//'
                while (position < input.length() && input.charAt(position) != '\n') {
                    position++;
                }
                // Continue loop to scan the next token after the comment
                continue;
            }
            if (current == '/' && position + 1 < input.length() && input.charAt(position + 1) == '*') {
                position += 2;
                while (position + 1 < input.length()) {
                    if (input.charAt(position) == '*' && input.charAt(position + 1) == '/') {
                        position += 2;
                        break;
                    }
                    position++;
                }
                continue;
            }

            // If it's just a single '/', it's the DIVIDE operator (handled in operators section)
            break;
        }

        // Check for End of File (EOF)
        if (position >= input.length()) {
            return new Token(TokenType.EOF, "");
        }

        char current = input.charAt(position);

        // 3. String Literals
        if (current == '"') {
            int start = position;
            position++; // Skip opening quote
            while (position < input.length()) {
                char c = input.charAt(position);
                if (c == '"') {
                    position++; // Skip closing quote
                    break;
                } else if (c == '\\') {
                    // Handle escape sequence (e.g., \", \\)
                    position += 2;
                } else {
                    position++;
                }
            }
            return new Token(TokenType.STRING, input.substring(start, position));
        }
        if (current == '\'') {
            int start = position;
            position++; // skip opening quote
            if (position < input.length() && input.charAt(position) == '\\') {
                position += 2; // handle escaped char like '\n'
            } else {
                position++;
            }
            position++; // skip closing quote
            return new Token(TokenType.CHAR_LITERAL, input.substring(start, position));
        }

        // 4. Identifiers and Keywords
        if (Character.isLetter(current)) {
            int start = position;
            position++;
            while (position < input.length()) {
                char c = input.charAt(position);
                if (Character.isLetterOrDigit(c) || c == '_') {
                    position++;
                } else {
                    break;
                }
            }
            String lexeme = input.substring(start, position);
            TokenType type = keywords.getOrDefault(lexeme, TokenType.IDENTIFIER);
            return new Token(type, lexeme);
        }

        // 5. Integer Literals
        if (Character.isDigit(current)) {
            int start = position;
            position++;
            while (position < input.length()) {
                char c = input.charAt(position);
                if (Character.isDigit(c)) {
                    position++;
                } else {
                    break;
                }
            }
            return new Token(TokenType.INTEGER, input.substring(start, position));
        }

        // 6. Operators and Punctuation (with longest match lookahead)
        switch (current) {
            case '=':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2;
                    return new Token(TokenType.EQUAL, "==");
                } else {
                    position++;
                    return new Token(TokenType.ASSIGN, "=");
                }
            case '<':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2;
                    return new Token(TokenType.LESS_EQUAL, "<=");
                } else {
                    position++;
                    return new Token(TokenType.LESS, "<");
                }
            case '>':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2;
                    return new Token(TokenType.GREATER_EQUAL, ">=");
                } else {
                    position++;
                    return new Token(TokenType.GREATER, ">");
                }
            case '!':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2;
                    return new Token(TokenType.NOT_EQUAL, "!=");
                } else {
                    position++;
                    throw new RuntimeException("Unexpected character: '!' at position " + (position - 1));
                }
            case '+':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2;
                    return new Token(TokenType.PLUS_ASSIGN, "+=");
                } else if (position + 1 < input.length() && input.charAt(position + 1) == '+') {
                    position += 2;
                    return new Token(TokenType.INCREMENT, "++");
                } else {
                    position++;
                    return new Token(TokenType.PLUS, "+");
                }
            case '-':
                if (position + 1 < input.length() && input.charAt(position + 1) == '=') {
                    position += 2;
                    return new Token(TokenType.MINUS_ASSIGN, "-=");
                } else if (position + 1 < input.length() && input.charAt(position + 1) == '-') {
                    position += 2;
                    return new Token(TokenType.DECREMENT, "--");
                } else {
                    position++;
                    return new Token(TokenType.MINUS, "-");
                }
            case '&':
                if (position + 1 < input.length() && input.charAt(position + 1) == '&') {
                    position += 2;
                    return new Token(TokenType.AND, "&&");
                } else {
                    position++;
                    throw new RuntimeException("Unexpected character: '&' at position " + (position - 1));
                }
            case '|':
                if (position + 1 < input.length() && input.charAt(position + 1) == '|') {
                    position += 2;
                    return new Token(TokenType.OR, "||");
                } else {
                    position++;
                    throw new RuntimeException("Unexpected character: '|' at position " + (position - 1));
                }
            case '?':
                position++;
                return new Token(TokenType.TERNARY, "?");
            case '*':
                position++;
                return new Token(TokenType.MULTIPLY, "*");
            case '/':
                position++;
                return new Token(TokenType.DIVIDE, "/");
            case '(':
                position++;
                return new Token(TokenType.LEFT_PAREN, "(");
            case ')':
                position++;
                return new Token(TokenType.RIGHT_PAREN, ")");
            case ',':
                position++;
                return new Token(TokenType.COMMA, ",");
            case ';':
                position++;
                return new Token(TokenType.SEMICOLON, ";");
            case ':':
                position++;
                return new Token(TokenType.COLON, ":");
            case '{':
                position++;
                return new Token(TokenType.LEFT_BRACKET, "{");
            case '}':
                position++;
                return new Token(TokenType.RIGHT_BRACKET, "}");
            case '[':
                position++;
                return new Token(TokenType.LEFT_BRACE, "[");
            case ']':
                position++;
                return new Token(TokenType.RIGHT_BRACE, "]");
            case '.':
                position++;
                return new Token(TokenType.DOT, ".");
            case '@':
                position++;
                return new Token(TokenType.AT, "@");
            default:
                throw new RuntimeException("Unexpected character: '" + current + "' at position " + position);
        }
    }

    public List<Token> scanTokens() {
        List<Token> tokens = new ArrayList<>();
        Token token;
        do {
            token = nextToken();
            if (token.type != TokenType.EOF) {
                tokens.add(token);
            }
        } while (token.type != TokenType.EOF);
        return tokens;
    }
}