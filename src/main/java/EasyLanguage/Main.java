package EasyLanguage;
import EasyLanguage.eva.Natives;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.statements.EmptyStatement;
import EasyLanguage.exceptions.ParseException;
import EasyLanguage.lexer.Lexer;
import EasyLanguage.lexer.Token.BaseToken;
import EasyLanguage.parser.LanguageParser;

public class Main {

    private static class Init{
        int a=5;
        public void addNumber(Integer b){
            a+=b;
        }
        public void getName(){
            System.out.println("a");
        }
    }
    public static void main(String[] args) throws ParseException {
        EasyEnv env=EasyEnv.buildWithInjectMap();
        InjectMap injectMap=env.getInjectMap();
        Init init=new Init();
        injectMap.putVal("init",init);
        String code= """
                class C{
                    let b=2;
                    let d=5;
                }
                let x=C.new();   
                init.addNumber(toInt(5));             
                """;
        env.setRunCode(code);
        env.run();
        System.out.println( env.getVal("x").toString());
    }

}
