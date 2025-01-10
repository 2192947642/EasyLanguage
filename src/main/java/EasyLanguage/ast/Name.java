package EasyLanguage.ast;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.lexer.Token.BaseToken;
import EasyLanguage.type.UndefinedType;

//变量名节点
public class Name extends ASTLeaf {
    public Name(BaseToken t) { super(t); }
    public String name() {   return token().getText(); }
    public Object eval(Environment env) {//变量名查询,从当前环境向外查询
        Object value = env.get(name());
        if (value == null) return UndefinedType.getInstance();
        else return value;
    }
}
