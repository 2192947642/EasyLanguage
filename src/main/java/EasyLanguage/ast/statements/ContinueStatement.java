package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.ast.res.EvalResCode;
import EasyLanguage.eva.env.Environment;

import java.util.List;

public class ContinueStatement extends ASTList {


    public ContinueStatement(List<ASTNode> list) {
        super(list);
    }

    public Object eval(Environment env){
        return new EvalRes(EvalResCode.continueReturn,null);
    }
}
