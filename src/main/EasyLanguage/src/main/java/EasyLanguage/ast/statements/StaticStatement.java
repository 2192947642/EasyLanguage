package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.eva.env.Environment;

import java.util.List;

public class StaticStatement extends ASTList {
    public StaticStatement(List<ASTNode> list) {
        super(list);
    }
    public Object eval(Environment staticENV){
        for (ASTNode t: this) {
          t.eval(staticENV);
        }
        return null;
    }
}
