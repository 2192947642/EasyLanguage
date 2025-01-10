package EasyLanguage.ast;

import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;

import java.util.List;
import java.util.Iterator;

public class ASTList extends ASTNode {
    protected List<ASTNode> children;
    public ASTList(List<ASTNode> list) { children = list; }
    public ASTNode child(int i) { return children.get(i); }
    public int numChildren() { return children.size(); }
    public Iterator<ASTNode> children() { return children.iterator(); }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        String sep = "";
        for (ASTNode t: children) {
            builder.append(sep);
            sep = " ";
            builder.append(t.toString());
        }
        return builder.append(')').toString();
    }
    public String location() {
        for (ASTNode t: children) {
            String s = t.location();
            if (s != null)
                return s;
        }
        return null;
    }
    public Object eval(Environment env) {
        throw new EvalException("eval方法应该由子类覆盖调用,而不是调用父类: " + toString(), this);
    }
}
