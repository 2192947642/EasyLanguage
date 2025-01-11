package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;

import java.util.List;

public class ForStatement extends ASTList {

    public ForStatement(List<ASTNode> c) { super(c); }
    public ASTNode init() { return child(0); }
    public ASTNode update() { return child(2); }
    public ASTNode condition() { return child(1); }
    public BlockStatement body() { return (BlockStatement) child(3); }

    public String toString() {
        return "(for "  + "( " + init()+";"+update()+";"+condition()+" )" +  body()+")";
    }
    public Object eval(Environment callerEnv) {
        Object evalRes;
        Environment forEnv=new Environment(EnvTypeEnum.Block,callerEnv);
        init().eval(forEnv);

        for (;;) {
            Object c = condition().eval(forEnv);
            //如果循环条件为假
            if (c instanceof Double d&& d == 0)
                return null;
            evalRes = body().evalWithoutMakeBlockEnv(forEnv);//运行块
            if(evalRes instanceof EvalRes e){
                if(e.isReturn()){
                    return evalRes;
                }else if(e.isBreak()){//如果是break修饰符那么直接返回
                    return null;
                }
            }
            update().eval(forEnv);
        }
    }
}
