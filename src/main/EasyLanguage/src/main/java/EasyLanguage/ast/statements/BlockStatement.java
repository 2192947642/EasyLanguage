package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.type.UndefinedType;

import java.util.List;
//块语句
public class BlockStatement extends ASTList {//
    public BlockStatement(List<ASTNode> c) { super(c); }
    public Object eval(Environment env) {
        //引入块作用域
        Environment blockEnv=new Environment(EnvTypeEnum.Block,env);
        return this.evalWithoutMakeBlockEnv(blockEnv);
    }
    public Object evalWithoutMakeBlockEnv(Environment blockEnv) {
        for (ASTNode t: this) {
            if (!(t instanceof EmptyStatement)){//如果不是空语句那么进行执行
                if(t.eval(blockEnv) instanceof EvalRes evalRes){
                    return  evalRes;
                }
            }
        }
        return UndefinedType.getInstance();
    }
}
