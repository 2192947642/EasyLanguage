package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.ParameterList;
import EasyLanguage.eva.Function;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.type.UndefinedType;

import java.util.List;

public class ConstructorStatement extends ASTList {
    private static final String name="constructor";
    public ConstructorStatement(List<ASTNode> list) {
        super(list);
    }
    public ParameterList parameters() { return (ParameterList)child(0); }
    public BlockStatement body() { return (BlockStatement)child(1); }
    public String toString() {
        return "(constructor "   + parameters() + " " + body() + ")";
    }
    //在当前环境中新建方法, 将函数名 及他的调用方法添加到函数调用中
    public Object eval(Environment env) {
        env.putNew(name, new Function(parameters(), body(), env,true));
        return UndefinedType.getInstance();
    }
}
