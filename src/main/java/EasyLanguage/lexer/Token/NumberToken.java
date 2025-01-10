package EasyLanguage.lexer.Token;


public class NumberToken extends BaseToken {
    private double value;//值

    public NumberToken(int line, double v) {
        super(line);
        this.tokenTypeEnum= TokenTypeEnum.number;
        value = v;
    }
    public String getText() { return Double.toString(value); }
    public double getNumber() { return value; }
}