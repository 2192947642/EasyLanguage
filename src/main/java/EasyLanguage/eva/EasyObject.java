package EasyLanguage.eva;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.type.UndefinedType;

import java.util.ArrayList;
import java.util.List;

public class EasyObject {
    protected Environment env;
    public EasyObject(Environment e) { env = e; }
    @Override
    public String toString() {
          return env.toString();
    }
    public Object read(String member) {
        List<EnvTypeEnum> circuitList=new ArrayList<>();//确保仅在当前对象以及当前对象的父类中进行读取信息
        circuitList.add(EnvTypeEnum.Function);
        circuitList.add(EnvTypeEnum.Global);
        circuitList.add(EnvTypeEnum.Inject);
        Object val=env.getCircuitByType(member,circuitList);
        if(val==null){
            val=UndefinedType.getInstance();
        }

        return val;
    }
    public Object write(String member, Object value)  {
          List<EnvTypeEnum> circuitList=new ArrayList<>();//确保仅在当前对象以及当前对象的父类中进行读取信息
          circuitList.add(EnvTypeEnum.Function);
          circuitList.add(EnvTypeEnum.Global);
          circuitList.add(EnvTypeEnum.Inject);
          Environment e=env.whereCircuitByType(member,circuitList);
          if(e==null) env.putNew(member,value);
          else e.putNew(member,value);
          return value;
    }

}
