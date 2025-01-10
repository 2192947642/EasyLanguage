package EasyLanguage.eva;

import EasyLanguage.ast.ASTNode;
import EasyLanguage.exceptions.EvalException;

import java.lang.reflect.Method;

public class NativeFunction {
    protected Method method;
    protected String name;
    protected int numParams;
    public NativeFunction(String n, Method m) {
        name = n;
        method = m;
        numParams = m.getParameterTypes().length;
    }
    @Override
    public String toString() { return "<native:" + hashCode() + ">"; }
    public int numOfParameters() { return numParams; } 
    public Object invoke(Object[] args, ASTNode tree) {
        try {
            return method.invoke(null, args);
        } catch (Exception e) {
            throw new EvalException("方法调用错误: " + name, tree);
        }
    }
}
