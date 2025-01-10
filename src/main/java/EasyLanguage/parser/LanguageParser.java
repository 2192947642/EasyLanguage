package EasyLanguage.parser;

import EasyLanguage.ast.*;
import EasyLanguage.ast.expressions.BinaryExpr;
import EasyLanguage.ast.expressions.NegativeExpr;
import EasyLanguage.ast.literals.ArrayLiteral;
import EasyLanguage.ast.literals.NumberLiteral;
import EasyLanguage.ast.literals.StringLiteral;
import EasyLanguage.ast.statements.*;
import EasyLanguage.exceptions.ParseException;
import EasyLanguage.lexer.Lexer;
import EasyLanguage.lexer.Token.BaseToken;

import java.util.HashSet;

import static EasyLanguage.parser.Parser.rule;

public class LanguageParser {
    HashSet<String> reserved = new HashSet<String>();//保留字列表
    Parser.Operators operators = new Parser.Operators();
    Parser preExpr = rule();//为expr
    //非终结符 基本构成元素
    //其后还会追加分支数组,空数组
    //追加可能出现的0次或者1次后缀
    Parser prePrimary=rule(PrimaryExpr.class);
    Parser primary = prePrimary
            .or(//rule().sep("(").ast(prePrimary).sep(")"),
                    rule().sep("(").ast(preExpr).sep(")"),
                    rule().number(NumberLiteral.class),
                    rule().identifier(Name.class, reserved),//生成变量名
                    rule().string(StringLiteral.class));//生成字符串字面量
    Parser factor=rule().or(rule(NegativeExpr.class).sep("-").ast(primary),primary);
    //表达式
    Parser expr = preExpr.expression(BinaryExpr.class, factor, operators);

    Parser letStatement=rule(LetStatement.class).sep("let").identifier(reserved).sep("=").ast(expr);
    Parser preStatement=rule();//防止非前项引用错误出现
    //提取块
    Parser block = rule(BlockStatement.class)
            .sep("{").option(preStatement)
            .repeat(rule().sep(";", BaseToken.EOL).option(preStatement))
            .sep("}");
    //其后还会有一个OPTION（poxfix)
    Parser simple = rule(PrimaryExpr.class).ast(expr);
    //类的静态初始化
    Parser bracketExpr = rule().sep("(").ast(expr).sep(")");
    Parser changeLine=rule().sep(BaseToken.EOL);
    Parser statement = preStatement.or(
            block,//块也可以单独定义
            rule(IfStatement.class).sep("if").ast(bracketExpr).repeat(changeLine).ast(block).repeat(changeLine)
                    .repeat(rule(ElifStatement.class).sep("elif").repeat(changeLine).ast(bracketExpr).ast(block).repeat(changeLine))
                    .option(rule(ElseStatement.class).sep("else").repeat(changeLine).ast(block)),
            rule(DoWhileStatement.class).sep("do").ast(block).sep("while").ast(bracketExpr),
            rule(ForStatement.class).sep("for").sep("(").or(expr, letStatement).sep(";").ast(expr).sep(";").ast(expr).sep(")").ast(block),
            rule(WhileStatement.class).sep("while").ast(bracketExpr).repeat(changeLine).ast(block),
            rule(BreakStatement.class).sep("break"),
            rule(ContinueStatement.class).sep("continue"),
            simple
            );

    //单个完整的语句或者是空语句
    Parser program = rule().or(statement, rule(EmptyStatement.class))
            .sep(";", BaseToken.EOL);
    //数组定义时[xxx ] 的xxx
    Parser elements = rule(ArrayLiteral.class)
            .ast(expr).repeat(rule().sep(",").ast(expr));//数组相关

    //单个参数
    Parser param = rule().identifier(reserved);//传入的标识符
    //多个参数
    Parser params = rule(ParameterList.class)
            .ast(param).repeat(rule().sep(",").ast(param));
    //方法参数列表,其内部可能含有多个参数
    Parser paramList = rule().sep("(").maybe(params).sep(")");
    //方法的定义
    Parser function = rule(FunctionStatement.class)
            .sep("function").identifier(reserved).ast(paramList).ast(block);
    Parser constructor = rule(ConstructorStatement.class)
            .sep("constructor").ast(paramList).ast(block);

    //类内部的成员语句
    //类的静态方法快
    Parser staticParser=rule(StaticStatement.class).sep("static").or(
            letStatement,//静态参数定义
            function,//静态函数定义
            block,//静态代码块
            expr//静态参数定义
    );
    //类定义提中可能含有的成员
    Parser member = rule().or(staticParser,letStatement,function,constructor, simple);

    //类定义体,初始化调用
    Parser classBody = rule(ClassBody.class).sep("{").option(member)
            .repeat(rule().sep(";", BaseToken.EOL).option(member))
            .sep("}");
    //类的总体定义
    Parser defineClass = rule(ClassStatement.class).sep("class").identifier(reserved)
            .option(rule().sep("extends").identifier(reserved))
            .ast(classBody);


    //方法调用语法分析
    Parser args = rule(Arguments.class)
            .ast(expr).repeat(rule().sep(",").ast(expr));
    //后缀,在构造方法中会被再次构造为一个or繁殖,1.函数调用(...args),2.数组调用[xxx],3.xxx

    Parser postfix = rule().sep("(").maybe(args).sep(")");
    //返回结果
    Parser toReturn=rule(ReturnStatement.class).sep("return").ast(expr);

    public LanguageParser() {
        reserved.add(">");
        reserved.add(">=");
        reserved.add("<");
        reserved.add("<=");
        reserved.add("=");
        reserved.add("==");
        reserved.add("!");
        reserved.add("!=");
        reserved.add("&&");
        reserved.add("||");

        reserved.add("+");
        reserved.add("+=");
        reserved.add("-");
        reserved.add("-=");

        reserved.add("*");
        reserved.add("/");
        reserved.add("%");


        reserved.add("(");
        reserved.add(")");
        reserved.add("[");
        reserved.add("]");
        //baseParser
        reserved.add(";");//添加保留字
        reserved.add("}");
        reserved.add("static");
        reserved.add("class");
        //reserved.add("constructor");
        reserved.add("extends");
        reserved.add("function");
        reserved.add("return");
        reserved.add("do");
        reserved.add("for");
        reserved.add("while");
        reserved.add("break");
        reserved.add("continue");
        reserved.add("if");
        reserved.add("else");
        reserved.add("elif");
        reserved.add("let");

        reserved.add(BaseToken.EOL);
        //！ > 算术运算符 > 关系运算符 > && > || > 赋值运算符
        operators.add("=", 1, Parser.Operators.RIGHT);
        operators.add("+=",1, Parser.Operators.RIGHT);
        operators.add("-=",1, Parser.Operators.RIGHT);

        operators.add("||",2,Parser.Operators.LEFT);
        operators.add("&&", 3, Parser.Operators.LEFT);

        operators.add("==", 4, Parser.Operators.LEFT);
        operators.add("!=", 4, Parser.Operators.LEFT);
        operators.add(">", 4, Parser.Operators.LEFT);
        operators.add("<", 4, Parser.Operators.LEFT);
        operators.add(">=", 4, Parser.Operators.LEFT);
        operators.add("<=", 4, Parser.Operators.LEFT);

        operators.add("+", 5, Parser.Operators.LEFT);
        operators.add("-", 5, Parser.Operators.LEFT);

        operators.add("*", 6, Parser.Operators.LEFT);
        operators.add("/", 6, Parser.Operators.LEFT);
        operators.add("%", 6, Parser.Operators.LEFT);
        //let Parser
        statement.insertChoice(letStatement);
        //classParser
        postfix.insertChoice(rule(Dot.class).sep(".").identifier(reserved));
        program.insertChoice(defineClass);
        //closeParser
        primary.insertChoice(rule(ClosureFunction.class).sep("$").ast(paramList).sep("=>").ast(block));
        //functionParser
        reserved.add(")");
        primary.repeat(postfix);
        simple.option(args);
        program.insertChoice(function);
        statement.insertChoice(toReturn);
        //arrayParser
        reserved.add("]");
        primary.insertChoice(rule().sep("[").maybe(elements).sep("]"));
        postfix.insertChoice(rule(ArrayRef.class).sep("[").ast(expr).sep("]"));
    }
    //
    public ASTNode parse(Lexer lexer) throws ParseException {
        //program含有三个分支 程序
        //方法定义
        //类定义
        //statement定义
        return program.parse(lexer);
    }
}
