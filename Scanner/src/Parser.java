import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<Scanner.Token> tokens;
    private int current = 0;

    public Parser(List<Scanner.Token> rawTokens) {
        this.tokens = new ArrayList<>();
        for (Scanner.Token token : rawTokens) {
            if (token.type != Scanner.TokenType.WHITESPACE &&
                    token.type != Scanner.TokenType.NEWLINE) {
                this.tokens.add(token);
            }
        }
    }

    public ParseTreeNode parse() {
        try {
            ParseTreeNode root = parseClassDecl();

            if (peek().type != Scanner.TokenType.EOF) {
                throw new RuntimeException("Unexpected tokens remaining at end of file.");
            }
            return root;
        } catch (Exception e) {
            System.out.println("[Parse Error]: " + e.getMessage());
            return null;
        }
    }

    private ParseTreeNode parseProgram() {
        ParseTreeNode node = new ParseTreeNode("Program", false, null);
        node.children.add(parseClassDecl());
        return node;
    }

    private ParseTreeNode parseClassDecl() {
        ParseTreeNode rootNode = consumeNode(Scanner.TokenType.PUBLIC);

        rootNode.children.add(consumeNode(Scanner.TokenType.CLASS));
        rootNode.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));
        rootNode.children.add(consumeNode(Scanner.TokenType.LEFT_BRACKET));
        rootNode.children.add(parseMethodDeclList());
        rootNode.children.add(consumeNode(Scanner.TokenType.RIGHT_BRACKET));

        return rootNode;
    }

    private ParseTreeNode parseMethodDeclList() {
        ParseTreeNode node = new ParseTreeNode("MethodDeclList", false, null);
        while (peek().type == Scanner.TokenType.PUBLIC) {
            node.children.add(parseMethodDecl());
        }
        return node;
    }

    private ParseTreeNode parseMethodDecl() {
        ParseTreeNode node = new ParseTreeNode("MethodDecl", false, null);
        node.children.add(consumeNode(Scanner.TokenType.PUBLIC));
        node.children.add(consumeNode(Scanner.TokenType.STATIC));
        Scanner.Token typeToken = peek();
        if (typeToken.type == Scanner.TokenType.VOID || typeToken.type == Scanner.TokenType.INT || typeToken.type == Scanner.TokenType.BOOLEAN) {
            node.children.add(consumeNode(typeToken.type));
        } else {
            throw new RuntimeException("Parse Error: Expect method return type (void or int) but found " + typeToken.type);
        }
        node.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));
        node.children.add(consumeNode(Scanner.TokenType.LEFT_PAREN));
        if (peek().type != Scanner.TokenType.RIGHT_PAREN) {
            node.children.add(parseFormalParamList());
        }
        node.children.add(consumeNode(Scanner.TokenType.RIGHT_PAREN));
        node.children.add(consumeNode(Scanner.TokenType.LEFT_BRACKET));
        node.children.add(parseStatementList());
        node.children.add(consumeNode(Scanner.TokenType.RIGHT_BRACKET));
        return node;
    }

    private ParseTreeNode parseFormalParamList() {
        ParseTreeNode node = new ParseTreeNode("FormalParamList", false, null);

        node.children.add(parseType());
        node.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));

        while (peek().type == Scanner.TokenType.COMMA) {
            node.children.add(consumeNode(Scanner.TokenType.COMMA));
            node.children.add(parseType());
            node.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));
        }

        return node;
    }

    private ParseTreeNode parseType() {
        ParseTreeNode node = new ParseTreeNode("Type", false, null);
        Scanner.Token token = peek();

        if (token.type == Scanner.TokenType.INT ||
                token.type == Scanner.TokenType.BOOLEAN ||
                token.type == Scanner.TokenType.IDENTIFIER) {
            node.children.add(consumeNode(token.type));

            if (peek().type == Scanner.TokenType.LEFT_BRACE) {
                node.children.add(consumeNode(Scanner.TokenType.LEFT_BRACE));
                node.children.add(consumeNode(Scanner.TokenType.RIGHT_BRACE));
            }
        } else {
            throw new RuntimeException("Parse Error: Expect type but found " + token.type);
        }
        return node;
    }

    private ParseTreeNode parseStatementList() {
        ParseTreeNode node = new ParseTreeNode("StatementList", false, null);
        while (isStatementStart(peek().type)) {
            node.children.add(parseStatement());
        }
        return node;
    }

    private boolean isStatementStart(Scanner.TokenType type) {
        return type == Scanner.TokenType.IDENTIFIER ||
                type == Scanner.TokenType.INT ||
                type == Scanner.TokenType.BOOLEAN ||
                type == Scanner.TokenType.IF ||
                type == Scanner.TokenType.WHILE ||
                type == Scanner.TokenType.RETURN ||
                type == Scanner.TokenType.LEFT_BRACKET;
    }

    private ParseTreeNode parseStatement() {
        ParseTreeNode node = new ParseTreeNode("Statement", false, null);
        Scanner.TokenType type = peek().type;

        if (type == Scanner.TokenType.IDENTIFIER) {
            node.children.add(parseAssignmentStmt());
        } else if (type == Scanner.TokenType.INT || type == Scanner.TokenType.BOOLEAN) {
            node.children.add(parseVarDeclStmt());
        } else if (type == Scanner.TokenType.IF) {
            node.children.add(parseIfStmt());
        } else if (type == Scanner.TokenType.WHILE) {
            node.children.add(parseWhileStmt());
        } else if (type == Scanner.TokenType.RETURN) {
            node.children.add(parseReturnStmt());
        } else if (type == Scanner.TokenType.LEFT_BRACKET) {
            node.children.add(parseBlockStmt());
        } else {
            throw new RuntimeException("Unexpected statement token: " + type);
        }
        return node;
    }

    private ParseTreeNode parseVarDeclStmt() {
        ParseTreeNode node = new ParseTreeNode("VarDeclStmt", false, null);
        node.children.add(parseType());
        node.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));
        node.children.add(consumeNode(Scanner.TokenType.ASSIGN));
        node.children.add(parseExpression());
        node.children.add(consumeNode(Scanner.TokenType.SEMICOLON));
        return node;
    }

    private ParseTreeNode parseAssignmentStmt() {
        ParseTreeNode node = new ParseTreeNode("AssignmentStmt", false, null);
        node.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));
        node.children.add(consumeNode(Scanner.TokenType.ASSIGN));
        node.children.add(parseExpression());
        node.children.add(consumeNode(Scanner.TokenType.SEMICOLON));
        return node;
    }

    private ParseTreeNode parseIfStmt() {
        ParseTreeNode node = new ParseTreeNode("IfStmt", false, null);
        node.children.add(consumeNode(Scanner.TokenType.IF));
        node.children.add(consumeNode(Scanner.TokenType.LEFT_PAREN));
        node.children.add(parseExpression());
        node.children.add(consumeNode(Scanner.TokenType.RIGHT_PAREN));
        node.children.add(parseStatement());
        if (peek().type == Scanner.TokenType.ELSE) {
            node.children.add(consumeNode(Scanner.TokenType.ELSE));
            node.children.add(parseStatement());
        }
        return node;
    }

    private ParseTreeNode parseWhileStmt() {
        ParseTreeNode node = new ParseTreeNode("WhileStmt", false, null);
        node.children.add(consumeNode(Scanner.TokenType.WHILE));
        node.children.add(consumeNode(Scanner.TokenType.LEFT_PAREN));
        node.children.add(parseExpression());
        node.children.add(consumeNode(Scanner.TokenType.RIGHT_PAREN));
        node.children.add(parseStatement());
        return node;
    }

    private ParseTreeNode parseReturnStmt() {
        ParseTreeNode node = new ParseTreeNode("ReturnStmt", false, null);
        node.children.add(consumeNode(Scanner.TokenType.RETURN));
        if (peek().type != Scanner.TokenType.SEMICOLON) {
            node.children.add(parseExpression());
        }
        node.children.add(consumeNode(Scanner.TokenType.SEMICOLON));
        return node;
    }

    private ParseTreeNode parseBlockStmt() {
        ParseTreeNode node = new ParseTreeNode("BlockStmt", false, null);
        node.children.add(consumeNode(Scanner.TokenType.LEFT_BRACKET));
        node.children.add(parseStatementList());
        node.children.add(consumeNode(Scanner.TokenType.RIGHT_BRACKET));
        return node;
    }

    private ParseTreeNode parseExpression() {
        ParseTreeNode node = new ParseTreeNode("Expression", false, null);
        node.children.add(parseRelationalExpr());
        return node;
    }

    private ParseTreeNode parseRelationalExpr() {
        ParseTreeNode node = new ParseTreeNode("RelationalExpr", false, null);
        node.children.add(parseAdditiveExpr());

        while (peek().type == Scanner.TokenType.LESS ||
                peek().type == Scanner.TokenType.LESS_EQUAL ||
                peek().type == Scanner.TokenType.GREATER ||
                peek().type == Scanner.TokenType.GREATER_EQUAL ||
                peek().type == Scanner.TokenType.EQUAL ||
                peek().type == Scanner.TokenType.NOT_EQUAL) {
            node.children.add(consumeNode(peek().type));
            node.children.add(parseAdditiveExpr());
        }
        return node;
    }

    private boolean isRelationalOperator(Scanner.TokenType type) {
        return type == Scanner.TokenType.EQUAL || type == Scanner.TokenType.NOT_EQUAL ||
                type == Scanner.TokenType.LESS || type == Scanner.TokenType.LESS_EQUAL ||
                type == Scanner.TokenType.GREATER || type == Scanner.TokenType.GREATER_EQUAL;
    }

    private ParseTreeNode parseAdditiveExpr() {
        ParseTreeNode node = new ParseTreeNode("AdditiveExpr", false, null);
        node.children.add(parseMultiplicativeExpr());
        while (peek().type == Scanner.TokenType.PLUS || peek().type == Scanner.TokenType.MINUS) {
            node.children.add(consumeNode(peek().type));
            node.children.add(parseMultiplicativeExpr());
        }
        return node;
    }

    private ParseTreeNode parseMultiplicativeExpr() {
        ParseTreeNode node = new ParseTreeNode("MultiplicativeExpr", false, null);
        node.children.add(parsePrimary());
        while (peek().type == Scanner.TokenType.MULTIPLY || peek().type == Scanner.TokenType.DIVIDE) {
            node.children.add(consumeNode(peek().type));
            node.children.add(parsePrimary());
        }
        return node;
    }

    private ParseTreeNode parsePrimary() {
        ParseTreeNode node = new ParseTreeNode("Primary", false, null);
        Scanner.TokenType type = peek().type;

        if (type == Scanner.TokenType.INTEGER ||
                type == Scanner.TokenType.STRING ||
                type == Scanner.TokenType.BOOLEAN_LITERAL) {
            node.children.add(consumeNode(type));
        } else if (type == Scanner.TokenType.IDENTIFIER) {
            if (lookaheadType(1) == Scanner.TokenType.LEFT_PAREN) {
                node.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));
                node.children.add(consumeNode(Scanner.TokenType.LEFT_PAREN));
                if (peek().type != Scanner.TokenType.RIGHT_PAREN) {
                    node.children.add(parseArgList());
                }
                node.children.add(consumeNode(Scanner.TokenType.RIGHT_PAREN));
            } else {
                node.children.add(consumeNode(Scanner.TokenType.IDENTIFIER));
            }
        } else if (type == Scanner.TokenType.LEFT_PAREN) {
            node.children.add(consumeNode(Scanner.TokenType.LEFT_PAREN));
            node.children.add(parseExpression());
            node.children.add(consumeNode(Scanner.TokenType.RIGHT_PAREN));
        } else {
            throw new RuntimeException("Unexpected primary expression token: " + type);
        }
        return node;
    }

    private ParseTreeNode parseArgList() {
        ParseTreeNode node = new ParseTreeNode("ArgList", false, null);
        node.children.add(parseExpression());
        while (peek().type == Scanner.TokenType.COMMA) {
            node.children.add(consumeNode(Scanner.TokenType.COMMA));
            node.children.add(parseExpression());
        }
        return node;
    }

    private Scanner.TokenType lookaheadType(int offset) {
        int idx = current + offset;
        if (idx >= tokens.size()) {
            return Scanner.TokenType.EOF;
        }
        return tokens.get(idx).type;
    }

    private Scanner.Token peek() {
        if (current >= tokens.size()) {
            return new Scanner.Token(Scanner.TokenType.EOF, "");
        }
        return tokens.get(current);
    }

    private Scanner.Token advance() {
        if (current < tokens.size()) current++;
        return tokens.get(current - 1);
    }


    private ParseTreeNode consumeNode(Scanner.TokenType type) {
        Scanner.Token token = peek();
        if (token.type == type) {
            advance();
            return new ParseTreeNode(token.type + ": " + token.lexeme, true, token.type);
        }
        throw new RuntimeException("Parse Error: Expect " + type + " but found " + token.type);
    }
}