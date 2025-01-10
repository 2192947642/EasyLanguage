package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.res.EvalRes;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.type.UndefinedType;

import java.util.ArrayList;
import java.util.List;

//条件变量语句
public class IfStatement extends ASTList {
    public IfStatement(List<ASTNode> c) { super(c); }
    public ASTNode condition() { return child(0); }
    public BlockStatement thenBlock() { return (BlockStatement) child(1); }
    public  List<ElifStatement> elseIfStatements() {
       List<ElifStatement> elifStatements= new ArrayList<>();
       children().forEachRemaining(node->{
           if(node instanceof ElifStatement elifStatement){
               elifStatements.add(elifStatement);
           }
       });
        return elifStatements;
    }
    public ASTNode elseBlock(){
        int size=this.numChildren();
        if(this.child(size-1) instanceof ElseStatement){
            return this.child(size-1);
        }
        return null;
    }
    public String toString() {
        //StringBuilder str= new StringBuilder();
        //for(ElifStatement elifStatement:elseIfStatements()){
        //    str.append(elifStatement.toString());
        //}
        //if(elseBlock()!=null){
        //    str.append(elseBlock().toString());
        //}
        return "(if " + condition() + " " + thenBlock()+")";
    }
    public Object eval(Environment env) {
      //  Object c = condition().eval(env);//条件判断.txt
        Object res=null;
        Object condition=condition().eval(env);
        boolean hasRun=false;
        if(condition instanceof Double && (Double) condition != 0)//如果可以进行运行
        {
            res=thenBlock().eval(env);
            hasRun=true;
        }
        else {
            for(ElifStatement elifStatement:elseIfStatements()){
                condition=elifStatement.condition().eval(env);
                if(condition instanceof Double && (Double) condition != 0){
                    res=elifStatement.thenBlock().eval(env);
                    hasRun=true;
                    break;
                }
            }
            if(!hasRun&&elseBlock()!=null){
                res=elseBlock().eval(env);
            }
        }
        if(res instanceof EvalRes evalRes){
            return evalRes;
        }
        return UndefinedType.getInstance();
    }
}
