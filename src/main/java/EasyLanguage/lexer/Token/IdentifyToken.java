package EasyLanguage.lexer.Token;

public class IdentifyToken extends BaseToken {
    private String text; //标识符名
    public IdentifyToken(int line, String id) {
        super(line);
        this.tokenTypeEnum= TokenTypeEnum.identify;
        text = id;
    }
    public String getText() { return text; }
}