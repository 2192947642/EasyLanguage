package EasyLanguage.ast;

import EasyLanguage.ast.statements.StaticStatement;
import EasyLanguage.eva.env.Environment;

import java.util.List;
//类的定义块 执行会进行类的初始化操作
public class ClassBody extends ASTList {
    public ClassBody(List<ASTNode> c) { super(c); }
    public Object eval(Environment env) {
      //  env.isClassInit=true;//标记当前环境正处在类初始化阶段,即当前所有的变量的定义都会定义在env环境内部
         for (ASTNode t: this){
             if(!(t instanceof  StaticStatement)){//执行不是静态的初始化
                 t.eval(env);
             }
         }
       // env.isClassInit=false;
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