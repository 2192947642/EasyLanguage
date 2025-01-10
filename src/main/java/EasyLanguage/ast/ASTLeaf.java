package EasyLanguage.ast;
import java.util.Iterator;
import java.util.ArrayList;

import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;
import EasyLanguage.lexer.Token.BaseToken;

public class ASTLeaf extends ASTNode {
    private static ArrayList<ASTNode> empty = new ArrayList<ASTNode>();
    protected BaseToken token;
    public ASTLeaf(BaseToken t) { token = t; }
    public ASTNode child(int i) { throw new IndexOutOfBoundsException(); }
    public int numChildren() { return 0; }
    public Iterator<ASTNode> children() { return empty.iterator(); }
    public String toString() { return token.getText(); }
    public String location() { return "at line " + token.getLineNumber(); }
    public BaseToken token() { return token; }
    public Object eval(Environment env) {
        throw new EvalException("eval方法应该由子类覆盖调用,而不是调用父类: " + toString(), this);
    }
}
