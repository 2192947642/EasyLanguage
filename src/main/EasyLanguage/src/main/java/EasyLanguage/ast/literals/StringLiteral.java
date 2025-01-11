package EasyLanguage.ast.literals;
import EasyLanguage.ast.ASTLeaf;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.lexer.Token.BaseToken;
//字符串字面量
public class StringLiteral extends ASTLeaf {
    public StringLiteral(BaseToken t) { super(t); }
    public String value() { return token().getText(); }
    public Object eval(Environment e) { return value(); }
}
