package EasyLanguage.lexer.Token;

public class StringToken extends BaseToken {
    private String literal;//字面量
    public StringToken(int line, String str) {
        super(line);
        this.tokenTypeEnum=TokenTypeEnum.string;
        literal = str;
    }

    public String getText() { return literal; }
}