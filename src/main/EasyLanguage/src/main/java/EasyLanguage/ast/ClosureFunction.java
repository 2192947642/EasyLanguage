package EasyLanguage.ast;

import EasyLanguage.ast.statements.BlockStatement;
import EasyLanguage.eva.Function;
import EasyLanguage.eva.env.Environment;

import java.util.List;
//闭包函数
public class ClosureFunction extends ASTList {
    public ClosureFunction(List<ASTNode> c) { super(c); }
    public ParameterList parameters() { return (ParameterList)child(0); }
    public BlockStatement body() { return (BlockStatement)child(1); }
    public String toString() {
        return "$( " + parameters() + " " + body() + ")";
    }
    public Object eval(Environment env) {
        return new Function(parameters(), body(), env);
    }
}
