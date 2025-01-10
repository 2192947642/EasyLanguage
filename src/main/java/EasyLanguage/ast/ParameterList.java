package EasyLanguage.ast;

import EasyLanguage.eva.env.Environment;

import java.util.List;
//参数列表 节点,执行将参数列表中index对应的参数键值对加入到当前的环境中
public class ParameterList extends ASTList {
    public ParameterList(List<ASTNode> c) { super(c); }
    public String name(int i) { return ((ASTLeaf)child(i)).token().getText(); }
    public int size() { return numChildren(); }
    public void eval(Environment env, int index, Object value) {

        env.putNew(name(index), value);
    }
}
