package EasyLanguage.ast;

import EasyLanguage.ast.statements.BlockStatement;
import EasyLanguage.eva.ClassInfo;
import EasyLanguage.eva.Function;
import EasyLanguage.eva.EasyObject;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;
import EasyLanguage.type.UndefinedType;

import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//点运算符的调用
public class Dot extends Postfix {
    public Dot(List<ASTNode> c) { super(c); }
    //dot 后的成员名 例如a.b的b
    public String name() { return ((ASTLeaf)child(0)).token().getText(); }
    public String toString() { return "." + name(); }
    public Object eval(Environment env, Object value)  {
        String member = name();//获得
        if (value instanceof ClassInfo ci) {//如果执行者是类的定义信息
            if ("new".equals(member)) {//如果是新建对象
                Environment environment = new Environment(EnvTypeEnum.Class,ci.environment());
                EasyObject so = new EasyObject(environment);//创建该对象 其内的属性用map来进行维护,也就是e
                environment.putNew("this", so);//将this添加到当前环境中
                environment.putNew("super",null);//
                initObject(ci, environment);//进行对象初始化,及先运行类的父类初始化块,再运行类的初始化代码块
                Object constructor = so.read("constructor");
                if(!(constructor instanceof Function)){//如果当前类的构造方法并没有显式声明 那么就创建一个默认的构造方法,并且隐式执行父类构造方法
                    constructor=ci.write("constructor",new Function(new ParameterList(new ArrayList<>()),new BlockStatement(new ArrayList<>()),environment,true,true));
                }
                return constructor;//返回构造方法
            }else{
                return ci.read(member);//否则就返回类的属性或者方法
            }
        }
        else if (value instanceof EasyObject) {//如果调用者是对象那么读取该属性并返回
            return ((EasyObject)value).read(member);
        }
        else if(value instanceof UndefinedType){
            throw new RuntimeException("不能访问未定义类型");
        } else if(value instanceof Object){
            Class<?> cl=value.getClass();
            try {
                // 尝试获取字段
                Field field = cl.getDeclaredField(member);
                field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                // 如果没有找到字段，那么说明访问的是方法 返回该值和访问的方法名
                HashMap hashMap=new HashMap();
                hashMap.put("instance",value);
                hashMap.put("method",member);
                return hashMap;
            }catch (InaccessibleObjectException inaccessibleObjectException){
                // 当访问 数组的.size()时 会出现该异常
                HashMap hashMap=new HashMap();
                hashMap.put("instance",value);
                hashMap.put("method",member);
                return hashMap;
            }
        }
        throw new EvalException("错误成员访问" + member, this);
    }
    protected void initObject(ClassInfo ci, Environment currentEnv) {//初始化对象,也就是将对象的代码块在该环境中进行运行
        if (ci.superClass() != null){//如果存在父类 那么先运行父类的代码块
            Environment fartherEnv=new Environment(EnvTypeEnum.Class);//父类对象的环境
            Environment globalEnv=currentEnv.getOuter();//获得当前环境的外部环境 也就是全局环境 父类环境需要在当前环境和全局环境之间
            fartherEnv.setOuter(globalEnv);
            currentEnv.setOuter(fartherEnv);
            EasyObject father=new EasyObject(fartherEnv);//构造父类方法对象
            currentEnv.putNew("super",father);
            fartherEnv.putNew("this",father);
            fartherEnv.putNew("super",null);
            initObject(ci.superClass(), fartherEnv);//如果存在父类对象那么就再初始化化父类对象
            Object constructor=fartherEnv.get("constructor");
            if(!(constructor instanceof Function)){//将构造方法写入到父类中
                constructor=ci.write("constructor",new Function(new ParameterList(new ArrayList<>()),new BlockStatement(new ArrayList<>()),fartherEnv,true,true));
            }
        }
        ci.body().eval(currentEnv);

    }
}