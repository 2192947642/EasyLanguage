package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.ast.res.EvalResCode;
import EasyLanguage.eva.env.Environment;

import java.util.List;

public class ReturnStatement extends ASTList {
    public ReturnStatement(List<ASTNode> list) {
        super(list);
    }
    public ASTNode result() { return child(0); }
    public Object eval(Environment env) {
       if(result() != null){
           return new EvalRes(EvalResCode.returnReturn,result().eval(env)) ;
       }
       return new EvalRes(EvalResCode.returnReturn,null);
    }
}
