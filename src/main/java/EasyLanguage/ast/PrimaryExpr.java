package EasyLanguage.ast;

import EasyLanguage.eva.env.Environment;

import java.util.List;
//基本构成元素
public class PrimaryExpr extends ASTList {
    public PrimaryExpr(List<ASTNode> c) { super(c); }
    public static ASTNode create(List<ASTNode> c) {
        return c.size() == 1 ? c.get(0) : new PrimaryExpr(c);
    }
    //操作数
    public ASTNode operand() { return child(0); }
    //后缀
    public Postfix postfix(int nest) {
        return (Postfix)child(numChildren() - nest - 1);
    }
    //是否有后缀
    public boolean hasPostfix(int nest) { return numChildren() - nest > 1; }
    //执行运算
    public Object eval(Environment env) {
        return evalSubExpr(env, 0);
    }
    public Object evalSubExpr(Environment env, int nest)  {
        if (hasPostfix(nest)) {
            Object target = evalSubExpr(env, nest + 1);
            return (postfix(nest)).eval(env, target);
        }
        else
            return (operand()).eval(env);
    }
}
