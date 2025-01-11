package EasyLanguage.ast.literals;

import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.eva.env.Environment;

import java.util.ArrayList;
import java.util.List;

public class ArrayLiteral extends ASTList {//数组字面量
    public ArrayLiteral(List<ASTNode> list) { super(list); }
    public int size() { return numChildren(); }
    public ArrayList<Object> eval(Environment env) {
        int s = numChildren();//获得数组对的长度
        ArrayList<Object> arrayList=new ArrayList(s);
        for (ASTNode t: this){
            arrayList.add(t.eval(env));
        }
        return arrayList;//返回
    }
}
