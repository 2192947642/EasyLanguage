package EasyLanguage.ast.statements;

import EasyLanguage.ast.ASTLeaf;
import EasyLanguage.ast.ASTList;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.ClassBody;
import EasyLanguage.eva.ClassInfo;
import EasyLanguage.eva.env.Environment;
import EasyLanguage.type.UndefinedType;

import java.util.List;
//类语句
public class ClassStatement extends ASTList {
    public ClassStatement(List<ASTNode> c) { super(c); }
    public String name() { return ((EasyLanguage.ast.ASTLeaf)child(0)).token().getText(); }
    public String superClass() {
        if (numChildren() < 3)
            return null;
        else
            return ((ASTLeaf)child(1)).token().getText();
    }
    //类的定义代码块
    public ClassBody body() { return (ClassBody)child(numChildren() - 1); }
    public String toString() {
        String parent = superClass();
        if (parent == null)
            parent = "*";
        return "(class " + name() + " " + parent + " " + body() + ")";
    }
    public Object eval(Environment env) {
        ClassInfo ci = new ClassInfo(this, env);//创建一个新的classInfo 并
        env.put(name(), ci);
        return UndefinedType.getInstance();
    }
}
