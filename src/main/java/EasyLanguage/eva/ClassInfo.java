package EasyLanguage.eva;

import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.ast.ClassBody;
import EasyLanguage.ast.statements.ClassStatement;
import EasyLanguage.exceptions.EvalException;
import EasyLanguage.type.UndefinedType;

public class ClassInfo {
    public final String constructorName = "new";
    protected ClassStatement definition;
    protected Environment environment;
    protected Environment staticEnvironment;
    protected ClassInfo superClass;
    public ClassInfo(ClassStatement cs, Environment env) {
        this.definition = cs;
        this.environment = env;
        this.staticEnvironment=new Environment(EnvTypeEnum.ClassStatis,env);
        Object obj = env.get(cs.superClass());
        body().staticEval(staticEnvironment);//运行静态代码块
        if (obj == null)
            superClass = null;
        else if (obj instanceof ClassInfo classInfo)
            superClass =classInfo;
        else
            throw new EvalException("未知父类: " + cs.superClass(), cs);
    }
    public String name() { return definition.name(); }
    public ClassInfo superClass() { return superClass; }
    public ClassBody body() { return definition.body(); }
    public Environment environment() { return environment; }
    public Object read(String member) {
        Object val=staticEnvironment.get(member);
        if(val==null){
            val= UndefinedType.getInstance();
        }
        return val;
    }
    public Object write(String member, Object value) {
        staticEnvironment.putNew(member,value);
        return value;
    }
    @Override
    public String toString() { return "class " + name() + ">"; }
}
