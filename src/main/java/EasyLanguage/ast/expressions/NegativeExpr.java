package EasyLanguage.ast.expressions;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;

import java.util.List;
//负数表达式
public class NegativeExpr extends ASTList {
    public NegativeExpr(List<ASTNode> c) { super(c); }
    public ASTNode operand() { return child(0); }
    public String toString() {
        return "-" + operand();
    }
    public Object eval(Environment env) {
        Object v = (operand()).eval(env);
        if (v instanceof Integer)
            return -(Integer) v;
        else
            throw new EvalException("错误的类型 -", this);
    }
}
