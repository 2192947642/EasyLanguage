package EasyLanguage.ast;

import EasyLanguage.ast.statements.StaticStatement;
import EasyLanguage.eva.env.Environment;

import java.util.List;
//类的定义块 执行会进行类的初始化操作
public class ClassBody extends ASTList {
    public ClassBody(List<ASTNode> c) { super(c); }
    public Object eval(Environment env) {
         for (ASTNode t: this){
             if(!(t instanceof  StaticStatement)){//执行不是静态的初始化
                 t.eval(env);
             }
         }
        return null;
    }
    public void staticEval(Environment staticEnv) {//当类被定义时运行
        for (ASTNode t: this){
            if(t instanceof StaticStatement){
                t.eval(staticEnv);
            }
        }
    }
}