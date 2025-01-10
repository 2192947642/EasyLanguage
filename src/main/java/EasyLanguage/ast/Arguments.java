package EasyLanguage.ast;

import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.eva.Function;
import EasyLanguage.eva.NativeFunction;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
//对函数方法进行调用
public class Arguments extends Postfix {
    public Arguments(List<ASTNode> c) { super(c); }
    public int size() { return numChildren(); }
    // 在调用者环境中，调用函数
    public Object eval(Environment callerEnv, Object value) {
        if (value instanceof NativeFunction func){//如果是java原生的方法
            int nparams = func.numOfParameters();
            if (size() != nparams)
                throw new EvalException("方法参数错误", this);
            Object[] args = new Object[nparams];
            int num = 0;
            for (ASTNode a: this) {
                args[num++] = a.eval(callerEnv);
            }
            return func.invoke(args, this);
        }
        else if (value instanceof Function func){//
            ParameterList params = func.parameters();//获得方法的参数
            // if (size() != params.size())//对比方法的参数数量是否匹配 如果不匹配那么报错
            //     throw new StoneException("方法参数数目不匹配错误", this);
            Environment funcEnv = func.makeEnv();//获得新建的方法环境，该环境的外部环境为函数定义环境
            int num = 0;
            for (ASTNode a: this) //循环调用该参数列表node 将参数传入到方法环境中
                params.eval(funcEnv, num++, a.eval(callerEnv));
            Object res=func.body().evalWithoutMakeBlockEnv(funcEnv);//以新建方法环境为基础运行方法体
            if(func.isConstructor()){
                return funcEnv.getInEnv("this", Collections.singletonList(EnvTypeEnum.Class));
            }
            else if(res instanceof EvalRes evalRes&&evalRes.isReturn()){//如果返回的是一个RETURN 的value那么对方法值进行返回
                return evalRes.value;
            }
            return null;
        }
        else if(value instanceof HashMap<?,?> hashMap){
            String methodName= (String) hashMap.get("method");
            Object instance=hashMap.get("instance");
            Object[] args=this.children.stream().map(item->item.eval(callerEnv)).toArray();
            Class[] argTypes=new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i]=args[i].getClass();
            }
            try{
                Method method=instance.getClass().getDeclaredMethod(methodName,argTypes);
                method.setAccessible(true);
                return method.invoke(instance, args);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("在该注入类中没有找到相关方法");
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        else throw new EvalException("方法调用错误");
    }
}
