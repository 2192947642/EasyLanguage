package EasyLanguage.lexer.Token;

import EasyLanguage.exceptions.EvalException;

public abstract class BaseToken {
    protected TokenTypeEnum tokenTypeEnum=TokenTypeEnum.unKnown;
    public static final BaseToken EOF = new BaseToken(-1){}; // 文件尾
    public static final String EOL = "\\n";          // 行尾
    private final int lineNumber;
    protected BaseToken(int line) {
        lineNumber = line;
    }
    public TokenTypeEnum getTokenTypeEnum() {
        return tokenTypeEnum;
    }

    public int getLineNumber() { return lineNumber; }
    public Boolean isIdentify(){
        return  this.tokenTypeEnum==TokenTypeEnum.identify;
    }
    public Boolean isString(){
        return  this.tokenTypeEnum==TokenTypeEnum.string;
    }
    public Boolean isNumber(){
        return this.tokenTypeEnum==TokenTypeEnum.number;
    }

    public double getNumber() { throw new EvalException("不是numberToken类型,获得数值失败"); }
    public String getText() { return ""; }
}
