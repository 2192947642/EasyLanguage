package EasyLanguage.eva;

import EasyLanguage.eva.env.EnvTypeEnum;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.ast.ParameterList;
import EasyLanguage.ast.statements.BlockStatement;

public class Function {
    protected boolean isAuto=false;//是否为生成的构造方法
    protected  boolean isConstructor=false;
    protected ParameterList parameters;
    protected BlockStatement body;
    protected Environment env;//当前方法的定义环境
    public Function(ParameterList parameters, BlockStatement body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }
    public Function(ParameterList parameters, BlockStatement body, Environment env,Boolean isConstructor) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
        this.isConstructor=isConstructor;
    }
    public Function(ParameterList parameters, BlockStatement body, Environment env,Boolean isConstructor,Boolean isAuto) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
        this.isConstructor=isConstructor;
        this.isAuto=isAuto;
    }
    public Boolean isAuto() { return isAuto; }
    public Boolean isConstructor() { return isConstructor; }
    public ParameterList parameters() { return parameters; }
    public BlockStatement body() { return body; }
    //新建一个环境,作为当前方法环境的子环境,以此实现递归基础
    public Environment makeEnv() { return new Environment(EnvTypeEnum.Function,env); }
    @Override public String toString() { return "<fun:" + hashCode() + ">"; }
}
