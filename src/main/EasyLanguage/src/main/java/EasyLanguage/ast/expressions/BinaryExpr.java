package EasyLanguage.ast.expressions;

import EasyLanguage.util.Util;
import EasyLanguage.ast.*;
import EasyLanguage.eva.ClassInfo;
import EasyLanguage.eva.EasyObject;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;
import EasyLanguage.type.UndefinedType;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
//二元运算
public class BinaryExpr extends ASTList {
    public BinaryExpr(List<ASTNode> c) { super(c); }
    public ASTNode left() { return child(0); }
    public String operator() {
        return ((ASTLeaf)child(1)).token().getText();
    }
    public ASTNode right() { return child(2); }
    public Object eval(Environment env) {
        String op = operator();
        if ("=".equals(op)) {
            Object right = (right()).eval(env);
            return computeAssign(env, right);
        }else if("+=".equals(op)){
            Object left = left().eval(env);
            Object right = right().eval(env);
            return computeAssign(env,computeOp(left,"+",right));
        }else if("-=".equals(op)){
            Object left = left().eval(env);
            Object right = right().eval(env);
            return computeAssign(env,computeOp(left,"-",right));
        }
        else {
            Object left = left().eval(env);
            Object right = right().eval(env);
            return computeOp(left, op, right);
        }
    }

    protected Object computeAssign(Environment env, Object rvalue) {
        ASTNode le = left();
        if (le instanceof PrimaryExpr p) {
            if (p.hasPostfix(0) && p.postfix(0) instanceof ArrayRef) {
                Object a =p.evalSubExpr(env, 1);
                if (a instanceof ArrayList arrayList) {
                    ArrayRef aref = (ArrayRef)p.postfix(0);
                    int index = Util.ObjectToInt(aref.index().eval(env));
                    while (arrayList.size()<=index){//实现自动扩容
                        arrayList.add(0);
                    }
                    arrayList.set(index,rvalue);     //如果是数组类型
                    return rvalue;
                }
            }
            else if (p.hasPostfix(0) && p.postfix(0) instanceof Dot) {
                    Dot dotP=(Dot)p.postfix(0);
                    Object t = p.evalSubExpr(env, 1);
                    if (t instanceof EasyObject easyObject)
                        return setField(easyObject, dotP, rvalue);
                    else if(t instanceof ClassInfo classInfo){
                        return setField(classInfo,dotP,rvalue);
                    }else if(t instanceof UndefinedType){
                        throw new EvalException("类型错误", this);
                    }else if(t instanceof Object){
                        try {
                            String name=dotP.name();
                            Field field= t.getClass().getField(name);
                            field.setAccessible(true);
                            field.set(t,rvalue);
                            return rvalue;
                        }catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }
            }
            throw new EvalException("类型错误", this);
        }
        else if (le instanceof Name name) {
        //   if(env.isClassInit){//如果是类初始化阶段 那么变量赋值仅会发生在该实体类的环境中,不会向外扩散
        //       env.putNew(((Name)le).name(), rvalue);
        //   }
        //   else if(env.getEnvType()== EnvTypeEnum.ClassStatis){//如果是static环境那么变量赋值只会发生在static环境中 不会向外进行赋值
        //       env.putNew(((Name)le).name(), rvalue);
        //   }
            // else
            //如果是变量赋值 那么先查找是否存在该变量 如果不存在那么就抛出错误
           Environment environment=env.where(name.name());
           if(environment==null){
               throw new EvalException("变量尚未定义");
           }
           else{
               environment.putNew(name.name(), rvalue);
           }
           return rvalue;
        }
        else
            throw new EvalException("左值赋值错误", this);
    }

    protected Object setField(EasyObject obj, Dot expr, Object rvalue) {
        return obj.write(expr.name(), rvalue);
    }
    protected  Object setField(ClassInfo obj, Dot expr, Object rvalue){
        return obj.write(expr.name(), rvalue);
    }
    protected Object computeOp(Object left, String op, Object right) {
        Object leftVal=left;
        Object rightVal=right;
        if(leftVal instanceof Integer i) leftVal=Double.valueOf(i);
        if(rightVal instanceof Integer i) rightVal=Double.valueOf(i);
        if (leftVal instanceof Double leftD&& rightVal instanceof Double rightD) {
            return computeNumber(leftD, op, rightD);
        } else
        if (op.equals("+"))
            return leftVal + String.valueOf(rightVal);
        else if (op.equals("==")) {
            if (left == null)
                return right == null ? 1.0 : 0;
            else
                return left.equals(right) ? 1.0 : 0;
        }
        else
            throw new EvalException("运算类型错误", this);
    }
    protected Double computeNumber(Double left, String op, Double right) {
        double a = left;
        double b = right;
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            case "%" -> a % b;
            case "==" -> a == b ? 1.0 : 0;
            case "<=" -> a <= b ? 1.0 : 0;
            case ">=" -> a >= b ? 1.0 : 0;
            case "!=" -> a != b ? 1.0 : 0;
            case "&&" -> a != 0 && b != 0 ? 1.0 : 0;
            case "||" -> a != 0 || b != 0 ? 1.0 : 0;
            case ">" -> a > b ? 1.0 : 0;
            case "<" -> a < b ? 1.0 : 0;
            default -> throw new EvalException("运算符错误", this);
        };
    }
}
