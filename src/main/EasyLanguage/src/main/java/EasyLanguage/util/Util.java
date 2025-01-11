package EasyLanguage.util;

import EasyLanguage.exceptions.EvalException;

public class Util {
    public static int ObjectToInt(Object d) {
        if(d instanceof  Float val){
            if (Math.ceil(val) == Math.floor(val) ) //将浮点数转换为整数
              return val.intValue();
        }
        if (d instanceof Double val) {
            if (Math.ceil(val) == Math.floor(val) ) //将浮点数转换为整数
              return val.intValue();
        }else if(d instanceof Integer val){
            return val;
        }
        else if(d instanceof  Boolean val){
            return val?1:0;
        }
        else if(d instanceof  String string){
            return Integer.parseInt(string);
        }


        if(d == null)
            return 0;
        throw new EvalException("转换为整数失败");
    }
}
