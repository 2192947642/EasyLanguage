package EasyLanguage.ast;

import EasyLanguage.util.Util;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;

import java.util.ArrayList;
import java.util.List;
//对数组或者字符串内部元素的访问
public class ArrayRef extends Postfix {
    public ArrayRef(List<ASTNode> c) { super(c); }
    public ASTNode index() { return child(0); }
    public Object eval(Environment env, Object value) {
        int index= Util.ObjectToInt(index().eval(env));
        if (value instanceof ArrayList<?> arrayList) {
            return arrayList.get(index);
        }else if(value instanceof String string){
            return string.charAt(index);
        }
        throw new EvalException("数组访问错误", this);
    }
    public String toString() { return "[" + index() + "]"; }
}
