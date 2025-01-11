package EasyLanguage;
import EasyLanguage.exceptions.ParseException;
public class Main {

    private static class Test{
        public static Integer InitFunc(Integer val){
            return val;
        }
        int a=5;
        public void addNumber(Integer b){
            a+=b;
        }
        public void getName(){
            System.out.println("a");
        }
    }
    public static void sayHello(){

    }

    public static void main(String[] args) throws ParseException {
        EasyEnv env=EasyEnv.buildWithInjectMap();//创建一个运行环境
        InjectMap injectMap=env.getInjectMap();//获得注入map
        Test test=new Test();
        injectMap.putVal("init",test);//传入一个值
        injectMap.putFunc("initFunc",Test.class,"InitFunc",Integer.class);//注入一个方法
        //运行代码
        String code= """
                init.getName();
                let a=10;
                print(a);
                """;
        env.setRunCode(code);
        //
        env.run();
    }

}
