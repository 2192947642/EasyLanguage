package EasyLanguage.ast;
import EasyLanguage.eva.env.Environment;

import java.util.Iterator;

public abstract class ASTNode implements Iterable<ASTNode> {
    public abstract ASTNode child(int i);
    public abstract int numChildren();
    public abstract Iterator<ASTNode> children();
    public abstract String location();
    public Iterator<ASTNode> iterator() { return children(); }
    public abstract Object eval(Environment env);
}
