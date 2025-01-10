package EasyLanguage;

import EasyLanguage.eva.Natives;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;

import java.util.HashMap;

public class InjectMap{
    private Environment environment=new Environment(EnvTypeEnum.Inject);
    public void putVal(String key,Object value){
        environment.putNew(key,value);
    }
    public void putFunc(String name, Class<?> clazz,
                        String methodName, Class<?> ... params){
        Natives.append(environment, name, clazz, methodName, params);
    }
    public <T> T get(Object key){
        return (T) environment.values.get(key);
    }
    public void remove(String key){
       environment.values.remove(key);
    }
    public void clear(){
        environment.values.clear();
    }
    protected Environment getEnv(){
        return environment;
    }
}
