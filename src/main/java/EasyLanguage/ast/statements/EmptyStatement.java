package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;

import java.util.List;
//空语句
public class EmptyStatement extends ASTList {
    public EmptyStatement(List<ASTNode> c) { super(c); }
}
