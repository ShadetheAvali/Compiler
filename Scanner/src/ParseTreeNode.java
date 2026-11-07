import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {
    public String label;
    public boolean isTerminal;
    public Scanner.TokenType tokenType;
    public List<ParseTreeNode> children = new ArrayList<>();

    public ParseTreeNode(String label, boolean isTerminal, Scanner.TokenType tokenType) {
        this.label = label;
        this.isTerminal = isTerminal;
        this.tokenType = tokenType;
    }
}