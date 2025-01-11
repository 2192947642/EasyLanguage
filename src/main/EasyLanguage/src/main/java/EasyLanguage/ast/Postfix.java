package EasyLanguage.ast;

import EasyLanguage.eva.env.Environment;

import java.util.List;
//后缀 形如 func(???), arr[???],father.???
public abstract class Postfix extends ASTList {
    public Postfix(List<ASTNode> c) { super(c); }
    public abstract Object eval(Environment env, Object value) ;
}
