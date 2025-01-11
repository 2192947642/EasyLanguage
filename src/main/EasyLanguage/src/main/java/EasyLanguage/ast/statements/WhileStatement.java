package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.type.UndefinedType;

import java.util.List;
//循环语句
public class WhileStatement extends ASTList {

        public WhileStatement(List<ASTNode> c) { super(c); }
        public ASTNode condition() { return child(0); }
        public ASTNode body() { return child(1); }
        public String toString() {
            return "(while " + condition() + " " + body() + ")";
        }
        public Object eval(Environment env) {
            Object evalRes;
            for (;;) {
                Object c = condition().eval(env);
                //如果循环条件为假
                if (c instanceof Double && (Double) c == 0)
                    return UndefinedType.getInstance();
                else{
                    evalRes = body().eval(env);//运行块
                    if(evalRes instanceof EvalRes e){
                        if(e.isReturn()){
                            return  evalRes;
                        }else if(e.isBreak()){//如果是break修饰符那么直接返回
                            return UndefinedType.getInstance();
                        }
                    }
                }
            }
        }
}
