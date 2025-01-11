package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTLeaf;
import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.ParameterList;
import EasyLanguage.eva.Function;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.type.UndefinedType;

import java.util.List;
// 函数定义语句
public class FunctionStatement extends ASTList {
    public FunctionStatement(List<ASTNode> c) { super(c); }
    public String name() { return ((ASTLeaf)child(0)).token().getText(); }

    public ParameterList parameters() { return (ParameterList)child(1); }
    public BlockStatement body() { return (BlockStatement)child(2); }
    public String toString() {
        return "(function " + name() + " " + parameters() + " " + body() + ")";
    }
    //在当前环境中新建方法, 将函数名 及他的调用方法添加到函数调用中
    public Object eval(Environment env) {
        env.putNew(name(), new Function(parameters(), body(), env));
        return UndefinedType.getInstance();
    }
}
