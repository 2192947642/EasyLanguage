package EasyLanguage.ast.literals;
import EasyLanguage.ast.ASTLeaf;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.lexer.Token.BaseToken;
//数字字面量
public class NumberLiteral extends ASTLeaf {
    public NumberLiteral(BaseToken t) { super(t); }
    public double value() { return token().getNumber(); }
    public Object eval(Environment e) { return value();}
    public String toString(){
        double val=value();
        if(Math.floor(val)==Math.ceil(val)){
            return String.valueOf((int)val);
        }
        return Double.toString(val);
    }
}
