package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;

import java.util.List;

public class ElifStatement extends ASTList {
    public ElifStatement(List<ASTNode> list) {
        super(list);
    }
    public ASTNode condition() { return child(0); }
    public BlockStatement thenBlock() { return (BlockStatement) child(1); }
    public String toString(){
        return "(elif " + condition() + " " + thenBlock() + ")";
    }
}
