package EasyLanguage.exceptions;

import EasyLanguage.lexer.Token.BaseToken;

import java.io.IOException;

public class ParseException extends Exception {
    public ParseException(BaseToken t) {
        this("", t);
    }
    public ParseException(String msg, BaseToken t) {
        super("语法错误" + location(t) + ". " + msg);
    }
    private static String location(BaseToken t) {
        if (t == BaseToken.EOF)
            return "最后一行";
        else
            return "\"" + t.getText() + "\"在第" + t.getLineNumber()+"行";
    }
    public ParseException(IOException e) {
        super(e);
    }
    public ParseException(String msg) {
        super(msg);
    }
}
