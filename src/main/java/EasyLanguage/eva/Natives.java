package EasyLanguage.eva;

import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.EvalException;

import javax.swing.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class Natives {
    public Environment environment(Environment env) {
        appendNatives(env);
        return env;
    }
    protected void appendNatives(Environment env) {
        Natives.append(env, "print", Natives.class, "print", Object.class);
        Natives.append(env, "input", Natives.class, "input");
        Natives.append(env, "length", Natives.class, "length", ArrayList.class);
        Natives.append(env,"strlen", Natives.class,"strlen",String.class);
        Natives.append(env, "toInt", Natives.class, "toInt", Object.class);
        Natives.append(env, "toFloat", Natives.class, "toFloat", Object.class);
        Natives.append(env, "toDouble", Natives.class, "toDouble", Object.class);
        Natives.append(env, "toLong", Natives.class, "toLong", Object.class);

        Natives.append(env,"format", Natives.class, "format", String.class, Object.class);
    }
    public static void append(Environment env, String name, Class<?> clazz,
                          String methodName, Class<?> ... params) {
        Method m;
        try {
            m = clazz.getMethod(methodName, params);
        } catch (Exception e) {
            throw new EvalException("找不到native函数: "
                                     + methodName);
        }
        env.put(name, new NativeFunction(methodName, m));
    }

    // native methods
    public static String format(String string,Object object){
        return String.format(string,object);
    }
    public static int print(Object obj) {
        System.out.println(obj.toString());
        return 0;
    }
    public static Object input() {
        String str=JOptionPane.showInputDialog(null);
        try {
            return Double.parseDouble(str);
        }catch (Exception e){
            return str;
        }
    }
    public static int length(ArrayList s) { return s.size(); }
    public static int strlen(String s){
        return s.length();
    }
    public static Double toDouble(Object value){
        return Double.parseDouble(value.toString());
    }
    public static Long toLong(Object value){
        return Long.parseLong(value.toString());
    }
    public static Float toFloat(Object value){
        return Float.parseFloat(value.toString());
    }
    public static Integer toInt(Object value){
        return toFloat(value).intValue();
    }

}
