package EasyLanguage.eva.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Environment {
    public HashMap<String,Object> values;
    // public boolean isClassInit=false;//当前环境是否是class类的初始化环境
    //设置该变量是为了解决当 初始化class时例如子类有a=6 他的父类存在a时 会将父类a的值赋值为6 而子类不会新增属性
    protected Environment outer;
    private final EnvTypeEnum envTypeEnum;
    public String toString(){
        StringBuilder stringBuilder=new StringBuilder("{");
        List<String> strs=new ArrayList<>();
        for(String key:values.keySet()){
            if(!key.equals("this")){
                strs.add(key+":"+values.get(key));
            }
        }
        String str= String.join(",\n",strs);
        stringBuilder.append(str);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
    public EnvTypeEnum getEnvType(){
        return this.envTypeEnum;
    }
    public Environment(EnvTypeEnum envTypeEnum) {
        this.values=new HashMap<>();
        this.envTypeEnum=envTypeEnum;
        this.outer=null;

    }
    public Environment(EnvTypeEnum envTypeEnum,Environment outer) {
        values = new HashMap<>();
        this.envTypeEnum=envTypeEnum;
        this.outer = outer;
    }
    public void setOuter(Environment e) { outer = e; }

    public Environment getOuter() {
        return outer;
    }

    //从当前环境向外查找值
    public Object get(String name) {
        Object v = values.get(name);
        if (v == null && outer != null)
            return outer.get(name);
        else
            return v;
    }
    public Object getNow(String name){
        return values.get(name);
    }
    public Object getInEnv(String name,List<EnvTypeEnum> enums){
          Environment environment=whereInTypes(name,enums);
          return environment==null? null :environment.getNow(name);
    };
    public Object getCircuitByType(String name, List<EnvTypeEnum> circuitList){
        Environment environment=whereCircuitByType(name,circuitList);
        return environment==null? null :environment.getNow(name);
    }
    public Object getWithoutEnv(String name, List<EnvTypeEnum> enums){
        Environment environment=whereOutTypes(name,enums);
        return environment==null? null :environment.getNow(name);
    }

    //在当前环境新建变量
    public void putNew(String name, Object value) { values.put(name, value); }
    //从当前环境向外查找变量存在的位置,如果都不存在那么就在当前环境新建,否则更新找到的环境里的值
    public void put(String name, Object value) {
        Environment e = where(name);
        if (e == null)
            e = this;
        e.putNew(name, value);
    }
    public Environment whereCircuitByType(String name, List<EnvTypeEnum>circuitList ){
        if(circuitList.contains(this.envTypeEnum)){//如果当前的环境符合断路环境那么返回null
            return null;
        }
        if(this.getNow(name)!=null){//如果当前的
            return this;
        }
        else return outer.whereOutTypes(name,circuitList);
    };
    public Environment whereOutTypes(String name,List<EnvTypeEnum> outLists){
        if((!outLists.contains(this.envTypeEnum))&&this.getNow(name)!=null){//如果当前的
            return this;
        }
        else if(outer==null){
            return null;
        }
        else return outer.whereOutTypes(name,outLists);
    }
    public Environment whereInTypes(String name,List<EnvTypeEnum> inLists){
        if(inLists.contains(this.envTypeEnum)&&this.getNow(name)!=null){//如果当前的
                return this;
        }
        else if(outer==null){
            return null;
        }
        else return outer.whereInTypes(name,inLists);
    }
    //查找包含变量名的环境
    public Environment where(String name) {//查找包含变量的环境
        if (values.get(name) != null)
            return this;
        else if (outer == null)
            return null;
        else
            return (outer).where(name);
    }

}
