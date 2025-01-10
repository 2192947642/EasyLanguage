package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTNode;

import java.util.List;

public class ElseStatement extends BlockStatement{
    public ElseStatement(List<ASTNode> c) {
        super(c);
    }
    public BlockStatement thenBlock() { return (BlockStatement) child(0); }
    public String toString(){
        return "(else "+thenBlock().toString()+")";
    }
}
