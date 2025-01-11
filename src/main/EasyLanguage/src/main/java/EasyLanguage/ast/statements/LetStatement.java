package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTLeaf;
import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;

import java.util.List;

//使用let 声名的变量一定会在当前的环境中赋值,为了解决解决不加let时
//当内部环境声明变量时 若外部环境存在该变量会导致声明失败 修改外部环境变量的问题
public class LetStatement extends ASTList {

    public LetStatement(List<ASTNode> list) {
        super(list);
    }
    public String name(){
        return ((ASTLeaf) child(0)).token().getText();
    }
    //初始值
    public ASTNode initializer(){
        return child(1);//初始化的值
    }
    public String toString(){
        return  "(let"+" " +name()+" = "+initializer()+")";
    }
    public Object eval(Environment env){
        Object value=initializer().eval(env);//获得当前value的值
        Object hasValue=env.getNow(name());
        if(hasValue!=null){
            throw new EvalException("变量名在当前环境重复定义");
        }
        env.putNew(name(),value);//为当前作用域赋值
        return value;
    }

}
