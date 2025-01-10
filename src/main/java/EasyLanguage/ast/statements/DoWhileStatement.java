package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.eva.env.Environment;

import java.util.List;

public class DoWhileStatement extends ASTList {

    public DoWhileStatement(List<ASTNode> c) { super(c); }
    public ASTNode condition() { return child(1); }
    public ASTNode body() { return child(0); }
    public String toString() {
        return "(do "  + " " + body() +  condition()+")";
    }
    public Object eval(Environment env) {
        Object evalRes;
        for (;;) {
                evalRes = body().eval(env);//运行块
                if(evalRes instanceof EvalRes e){
                    if(e.isReturn()){
                        return evalRes;
                    }else if(e.isBreak()){//如果是break修饰符那么直接返回
                        return null;
                    }
                }
            Object c = condition().eval(env);
            //如果循环条件为假
            if (c instanceof Double && (Double) c == 0)
                return null;
        }
    }
}
