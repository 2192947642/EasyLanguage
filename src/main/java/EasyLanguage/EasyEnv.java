package EasyLanguage;

import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.statements.EmptyStatement;
import EasyLanguage.eva.Natives;
import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.exceptions.ParseException;
import EasyLanguage.lexer.Lexer;
import EasyLanguage.lexer.Token.BaseToken;
import EasyLanguage.parser.LanguageParser;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.HashMap;

public class EasyEnv {
    private InjectMap injectMap=null;
    private Environment environment=new Natives().environment(new Environment(EnvTypeEnum.Global));
    private LanguageParser languageParser=new LanguageParser();
    private Lexer lexer=new Lexer(new LineNumberReader(new StringReader("")));
    private String runCode="";
    public static EasyEnv buildWithInjectMap(){
        EasyEnv env=new EasyEnv("");
        InjectMap m=new InjectMap();
        env.setInjectMap(m);
        return env;
    }

    //当运行一次后 再次进行初始化,从而可以多次执行
    private void init(){
        environment=new Natives().environment(new Environment(EnvTypeEnum.Global));
        if(injectMap!=null) environment.setOuter(injectMap.getEnv());
        lexer.init(runCode);
    }
    public InjectMap getInjectMap(){
        return injectMap;
    }
    public void setInjectMap(InjectMap injectMap){
        this.injectMap=injectMap;
        environment.setOuter(injectMap.getEnv());
    }
    public EasyEnv(String initCode){
        setRunCode(initCode);
    }
    public void setRunCode(String code){
        runCode=code;
        lexer.init(code);
    }
    public <T> T getVal(String key){
        return (T) environment.get(key);
    }
    public void run() throws ParseException {
        init();
        while (lexer.peek(0) != BaseToken.EOF) {//当不是文件结束符时运行
            ASTNode t = languageParser.parse(lexer);
            if (!(t instanceof EmptyStatement)) {//如果不是空表达式那么进行运行
                t.eval(environment);
            }
        }
    }
}
