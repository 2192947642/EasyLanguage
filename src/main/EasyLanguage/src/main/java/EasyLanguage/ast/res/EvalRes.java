package EasyLanguage.ast.res;

public class EvalRes {
    public EvalRes(int evalResCode, Object value){
        this.evalResCode=evalResCode;
        this.value=value;
    }
    public int evalResCode;
    public Object value;

    public boolean isBreak(){
        return evalResCode== EvalResCode.breakReturn;
    }
    public boolean isContinue(){
        return evalResCode== EvalResCode.continueReturn;
    }
    public boolean isReturn(){
        return evalResCode== EvalResCode.returnReturn;
    }
}
