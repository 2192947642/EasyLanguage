package EasyLanguage.parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;

import EasyLanguage.exceptions.ParseException;
import EasyLanguage.ast.ASTNode;
import EasyLanguage.ast.ASTLeaf;
import EasyLanguage.ast.ASTList;
import EasyLanguage.lexer.Lexer;
import EasyLanguage.lexer.Token.BaseToken;
import EasyLanguage.lexer.Token.TokenTypeEnum;

//嵌套子类用来表示Parser需要处理的语法规则模式
public class Parser {

    protected static abstract class Element {
        //从词法分析器接收单词,并将解析结果add 到res中
        protected abstract void parse(Lexer lexer, List<ASTNode> res)
                throws ParseException;
        //返回是否匹配该语法
        protected abstract boolean match(Lexer lexer) throws ParseException;
    }

    protected static class Tree extends Element {
        protected Parser parser;
        protected Tree(Parser p) { parser = p; }
        //进行嵌套语法分析
        protected void parse(Lexer lexer, List<ASTNode> res)
                throws ParseException
        {
            res.add(parser.parse(lexer));
        }
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }

    protected static class OrTree extends Element {
        protected Parser[] parsers;//其内包含语法分支
        protected OrTree(Parser[] p) { parsers = p; }
        //在语法分析中，选择一个语法分支,解析成功后进行添加
        protected void parse(Lexer lexer, List<ASTNode> res)
                throws ParseException
        {
            Parser p = choose(lexer);
            if (p == null)
                throw new ParseException(lexer.peek(0));
            else
                res.add(p.parse(lexer));
        }
        //当前是否存在满足语法分析的语法分支
        protected boolean match(Lexer lexer) throws ParseException {
            return choose(lexer) != null;
        }
        //中查找一个符合的语法分支
        protected Parser choose(Lexer lexer) throws ParseException {
            for (Parser p: parsers)
                if (p.match(lexer))
                    return p;

            return null;
        }
        //添加新的语法分支
        protected void insert(Parser p) {
            Parser[] newParsers = new Parser[parsers.length + 1];
            newParsers[0] = p;
            System.arraycopy(parsers, 0, newParsers, 1, parsers.length);
            parsers = newParsers;
        }
    }
    //语法重复解析
    protected static class Repeat extends Element {
        protected Parser parser;
        protected boolean onlyOnce;//是否仅执行一次 对应BNF中的[]
        protected Repeat(Parser p, boolean once) { parser = p; onlyOnce = once; }
        protected void parse(Lexer lexer, List<ASTNode> res)
                throws ParseException
        {
            while (parser.match(lexer)) {
                ASTNode t = parser.parse(lexer);
                if (t.getClass() != ASTList.class || t.numChildren() > 0)
                    res.add(t);
                if (onlyOnce)
                    break;
            }
        }
        protected boolean match(Lexer lexer) throws ParseException {
            return parser.match(lexer);
        }
    }
    //token的解析
    protected static abstract class AToken extends Element {
        protected ASTNodeFactory factory;
        protected AToken(Class<? extends ASTLeaf> type) {
            if (type == null) //如果没有指定token的类型 那么指定为默认Leaf类型
                type = ASTLeaf.class;
            factory = ASTNodeFactory.get(type, BaseToken.class);//根据类型获取对应的工厂类,也就是可以当调用make方法时 该工厂类内部会通过反射创建一个对象
        }
        //对token进行解析
        protected void parse(Lexer lexer, List<ASTNode> res)
                throws ParseException
        {
            BaseToken t = lexer.read();
            if (test(t)) {//检测是否与相应的token类型所匹配,由子类进行实现
                ASTNode leaf = factory.make(t);
                res.add(leaf);
            }
            else
                throw new ParseException(t);
        }
        //是否匹配队首元素
        protected boolean match(Lexer lexer) throws ParseException {
            return test(lexer.peek(0));
        }
        protected abstract boolean test(BaseToken t);
    }
    //这些子类中每一个类型 对应着一个叶子节点类型
    protected static class IdToken extends AToken {//标识符token identify
        HashSet<String> reserved;
        protected IdToken(Class<? extends ASTLeaf> type, HashSet<String> r) {
            super(type);
            reserved = r != null ? r : new HashSet<String>();
        }
        protected boolean test(BaseToken t) {//当前token为标识符 并且不在保留字中
            return t.isIdentify()&& !reserved.contains(t.getText());
        }
    }

    protected static class NumToken extends AToken {//NumberToken
        protected NumToken(Class<? extends ASTLeaf> type) { super(type); }
        protected boolean test(BaseToken t) { return t.isNumber(); }
    }

    protected static class StrToken extends AToken {//stringToken
        protected StrToken(Class<? extends ASTLeaf> type) { super(type); }
        protected boolean test(BaseToken t) { return t.isString(); }
    }

    //叶子节点的解析
    protected static class Leaf extends Element {
        protected String[] tokens;
        protected Leaf(String[] pat) { tokens = pat; }
        protected void parse(Lexer lexer, List<ASTNode> res)
                throws ParseException
        {
            BaseToken t = lexer.read();
            if (t.isIdentify())
                for (String token: tokens)
                    if (token.equals(t.getText())) {
                        find(res, t);
                        return;
                    }
            if (tokens.length > 0)
                throw new ParseException( "希望收到token: "+tokens[0], t);
            else
                throw new ParseException(t);
        }
        //添加新的节点到语法树,其后可以通过判断token的类型来进行解析
        protected void find(List<ASTNode> res, BaseToken t) {
            res.add(new ASTLeaf(t));
        }
        protected boolean match(Lexer lexer) throws ParseException {
            BaseToken t = lexer.peek(0);
            if (t.isIdentify())
                for (String token: tokens)
                    if (token.equals(t.getText()))
                        return true;

            return false;
        }
    }
    //省略该token 即标记的string不存在于语法树中
    protected static class Skip extends Leaf {
        protected Skip(String[] t) { super(t); }
        protected void find(List<ASTNode> res, BaseToken t) {}
    }
    //优先权
    public static class Precedence {
        int value;//优先级，越高优先级越大
        boolean leftAssoc; // left associative 左结合
        public Precedence(int v, boolean a) {
            value = v; leftAssoc = a;
        }
    }
    //操作运算符的map集合,Precedence代表优先级
    public static class Operators extends HashMap<String,Precedence> {
        public static boolean LEFT = true;//左值运算
        public static boolean RIGHT = false;//右值运算
        public void add(String name, int prec, boolean leftAssoc) {
            put(name, new Precedence(prec, leftAssoc));
        }
    }
   //对表达式进行语法分析，包含算数运算符
    protected static class Expr extends Element {
        protected ASTNodeFactory factory;
        protected Operators ops;
        protected Parser exprParser;//
        protected Expr(Class<? extends ASTNode> clazz, Parser exp,
                       Operators map)
        {
            factory = ASTNodeFactory.getForASTList(clazz);
            ops = map;
            exprParser = exp;
        }
        //通过算术运算符的优先级分析来对表达式进行语法分析
        public void parse(Lexer lexer, List<ASTNode> res) throws ParseException {
            ASTNode right = exprParser.parse(lexer);//匹配一个右值运算对象
            Precedence prec=null;//代表当前进行运算的运算符对象
            while ((prec = nextOperator(lexer)) != null)//获得下一个运算符,如果没有那么说明运算完成
                right = doShift(lexer, right, prec.value);
            res.add(right);//将生成的表达式语法树根节点进行添加,eval该语法树来获得表达式的值
        }
        private ASTNode doShift(Lexer lexer, ASTNode left, int prec)//递归调用
                throws ParseException
        {
            ArrayList<ASTNode> list = new ArrayList<ASTNode>();//
            list.add(left);
            list.add(new ASTLeaf(lexer.read()));//将新读到的一个
            ASTNode right = exprParser.parse(lexer);
            Precedence next;
            while ((next = nextOperator(lexer)) != null
                    && rightIsExpr(prec, next))
                right = doShift(lexer, right, next.value);

            list.add(right);//将该层的表达式运算进行添加到list中
            return factory.make(list);//返回
        }
        //获得下一个操作运算符
        private Precedence nextOperator(Lexer lexer) throws ParseException {
            BaseToken t = lexer.peek(0);
            if (t.isIdentify())
                return ops.get(t.getText());
            else
                return null;
        }
        //检测
        private static boolean rightIsExpr(int prec, Precedence nextPrec) {
            if (nextPrec.leftAssoc)//如果下一个符号是左值运算 则优先级必须小于
                return prec < nextPrec.value;
            else
                return prec <= nextPrec.value;
        }
        protected boolean match(Lexer lexer) throws ParseException {
            return exprParser.match(lexer);
        }
    }

    public static final String factoryName = "create";
    //用来生成语法树节点
    protected static abstract class ASTNodeFactory {
        protected abstract ASTNode make0(Object arg) throws Exception;
        protected ASTNode make(Object arg) {
            try {
                return make0(arg);
            } catch (IllegalArgumentException e1) {
                throw e1;
            } catch (Exception e2) {
                throw new RuntimeException(e2); // this compiler is broken.
            }
        }
        //获得astList的构造工厂
        protected static ASTNodeFactory getForASTList(Class<? extends ASTNode> clazz) {
            ASTNodeFactory f = get(clazz, List.class);
            if (f == null)
                f = new ASTNodeFactory() {
                    protected ASTNode make0(Object arg) throws Exception {
                        List<ASTNode> results = (List<ASTNode>)arg;
                        if (results.size() == 1)
                            return results.get(0);
                        else
                            return new ASTList(results);
                    }
                };
            return f;
        }
        protected static ASTNodeFactory get(Class<? extends ASTNode> clazz,
                                            Class<?> argType)
        {
            if (clazz == null)
                return null;
             try {
                 final Method method = clazz.getMethod(factoryName,
                                                  new Class<?>[] { argType });
                 return new ASTNodeFactory() {
                     protected ASTNode make0(Object arg) throws Exception {
                         return (ASTNode)method.invoke(null, arg);
                     }
                 };
             } catch (NoSuchMethodException e) {}
            try {
                final Constructor<? extends ASTNode> c
                        = clazz.getConstructor(argType);
                return new ASTNodeFactory() {
                    protected ASTNode make0(Object arg) throws Exception {
                        return c.newInstance(arg);
                    }
                };
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected List<Element> elements;//其内部包含所有定义的语法规则
    protected ASTNodeFactory factory;//生成语法节点的工厂

    public Parser(Class<? extends ASTNode> clazz) {//clazz表示语法匹配时将要生成的语法节点
        reset(clazz);
    }
    protected Parser(Parser p) {
        elements = p.elements;
        factory = p.factory;
    }
    //从词法分析器接收单词,遍历铁路图的箭头,确定整条路径是否可以走通,如果可以走通那么代表语法分析成功
    //将创建并且返回相应的抽象语法树 否则代表发生语法错误,通过反复调用嵌套子类的方法,来判断路径是否能狗进行正确遍历
    public ASTNode parse(Lexer lexer) throws ParseException {
        ArrayList<ASTNode> results = new ArrayList<ASTNode>();
        for (Element e: elements)//根据语法规则进行匹配
            e.parse(lexer, results);
        return factory.make(results);//返回生成的语法树根节点
    }
    //对词法分析器进行模式匹配
    protected boolean match(Lexer lexer) throws ParseException {
        if (elements.isEmpty())
            return true;
        else {
            Element e = elements.get(0);
            return e.match(lexer);
        }
    }
    //生成一个新的语法匹配规则,并且当语法匹配成功时 不会生成节点
    public static Parser rule() { return rule(null);
    }
    //当语法匹配成功时 会生成新的语法节点
    public static Parser rule(Class<? extends ASTNode> clazz) {
        return new Parser(clazz);
    }
    //清空语法规则
    public Parser reset() {
        elements = new ArrayList<Element>();
        return this;
    }
    //清空语法规则并设置生成语法节点
    public Parser reset(Class<? extends ASTNode> clazz) {
        elements = new ArrayList<Element>();
        factory = ASTNodeFactory.getForASTList(clazz);
        return this;
    }
    //当匹配到数字时药生成的语法节点(向语法规则中添加终结符(数字字面量))
    public Parser number() {
        return number(null);
    }

    public Parser number(Class<? extends ASTLeaf> clazz) {
        elements.add(new NumToken(clazz));
        return this;
    }
    //添加标识符,在这里这个标识符指的是执行的变量名称或者是方法名称,类名称这样的（标识符并没有被细分）
    // reserved为保留字,无法被匹配
    public Parser identifier(HashSet<String> reserved) {
        return identifier(null, reserved);
    }

    public Parser identifier(Class<? extends ASTLeaf> clazz,
                             HashSet<String> reserved)
    {
        elements.add(new IdToken(clazz, reserved));
        return this;
    }
    public Parser string() {
        return string(null);
    }

    //当匹配到字符串时要生成的语法节点
    public Parser string(Class<? extends ASTLeaf> clazz) {
        elements.add(new StrToken(clazz));
        return this;
    }
    //添加token
    public Parser token(String... pat) {
        elements.add(new Leaf(pat));
        return this;
    }
    //添加可省略的语法规则
    public Parser sep(String... pat) {
        elements.add(new Skip(pat));
        return this;
    }
    //其后连接的语法规则
    public Parser ast(Parser p) {
        elements.add(new Tree(p));
        return this;
    }
    //选择分支
    public Parser or(Parser... p) {
        elements.add(new OrTree(p));
        return this;
    }
    //可能出现或者不出现
    public Parser maybe(Parser p) {
        Parser p2 = new Parser(p);
        p2.reset();
        elements.add(new OrTree(new Parser[] { p, p2 }));
        return this;
    }
    //重复至少0次此或者一次
    public Parser option(Parser p) {
        elements.add(new Repeat(p, true));
        return this;
    }
    //重复0次或者更多次
    public Parser repeat(Parser p) {
        elements.add(new Repeat(p, false));
        return this;
    }

    //添加新的表达式语法规则
    public Parser expression(Class<? extends ASTNode> clazz, Parser subexp,
                             Operators operators) {
        elements.add(new Expr(clazz, subexp, operators));
        return this;
    }
    //插入新的选择分支
    public Parser insertChoice(Parser p) {
        Element e = elements.get(0);
        if (e instanceof OrTree)
            ((OrTree)e).insert(p);
        else {
            Parser otherwise = new Parser(this);
            reset(null);
            or(p, otherwise);
        }
        return this;
    }
}
