package EasyLanguage.lexer;

import EasyLanguage.exceptions.ParseException;
import EasyLanguage.lexer.Token.IdentifyToken;
import EasyLanguage.lexer.Token.NumberToken;
import EasyLanguage.lexer.Token.StringToken;
import EasyLanguage.lexer.Token.BaseToken;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//词法分析器
public class Lexer {
    //1.判断是否捕获成功
    //2.捕获单行注释
    //3.捕获数字
    //4.捕获字符串
    //5.捕获标识符
    //6.捕获各种运算符和标点符号
    public static String regexPat
            = "\\s*((//.*)|([0-9]+(?:\\.[0-9]+)?)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")"
            + "|[A-Z_a-z][A-Z_a-z0-9]*|\\+=|-=|==|!=|<=|>=|=>|&&|\\|\\||\\p{Punct})?";
    private Pattern pattern = Pattern.compile(regexPat);
    private final ArrayList<BaseToken> queue = new ArrayList<BaseToken>();
    private boolean hasMore;
    private LineNumberReader reader;
    public void init(String code){
        this.reader=new LineNumberReader(new StringReader(code));
        this.queue.clear();
        this.hasMore=true;
    }

    public Lexer(Reader r) {
        hasMore = true;
        reader = new LineNumberReader(r);
    }
    public BaseToken read() throws ParseException {//取第一个并删除
        if (fillQueue(0))
            return queue.remove(0);
        else
            return BaseToken.EOF;
    }
    public BaseToken peek(int i) throws ParseException {//预取第i个,实现探路所需要
        if (fillQueue(i))
            return queue.get(i);
        else
            return BaseToken.EOF;
    }
    private boolean fillQueue(int i) throws ParseException {//检查当前队列是否存在第i个,如果不存在那么检测是否还有未读取的行,进行读取
        while (i >= queue.size())
            if (hasMore){
                readLine();
            }
            else
                return false;
        return true;
    }
    protected void readLine() throws ParseException {
        String line;
        try {
            line = reader.readLine();//会阻塞当前线程
        } catch (IOException e) {
            throw new ParseException(e);
        }
        if (line == null) {
            hasMore = false;
            return;
        }

        int lineNo = reader.getLineNumber();//获得当前代码所处的行数,创建token时传入
        Matcher matcher = pattern.matcher(line);
        // useTransparentBounds(boolean b) 方法用于设置此匹配器的区域边界的透明度。如果该参数为 true，则它将设置此匹配器使用透明边界，否则将使用不透明边界。
        matcher.useTransparentBounds(true).useAnchoringBounds(false);
        int pos = 0;
        int endPos = line.length();
        while (pos < endPos) {
            matcher.region(pos, endPos);
            if (matcher.lookingAt()) {
                addToken(lineNo, matcher);
                pos = matcher.end();
            }
            else
                throw new ParseException("token解析错误 " + lineNo);
        }
        queue.add(new IdentifyToken(lineNo, BaseToken.EOL));
    }
    //添加token
    protected void addToken(int lineNo, Matcher matcher) {
        String m = matcher.group(1);
        if (m != null) // if not a space
            if (matcher.group(2) == null) { // 不是注释
                BaseToken token;
                if (matcher.group(3) != null)//匹配到数字token
                    token = new NumberToken(lineNo, Double.parseDouble(m));
                else if (matcher.group(4) != null)//匹配到字符串token
                    token = new StringToken(lineNo, toStringLiteral(m));
                else   //创建一个保留字token并且添加到
                    token = new IdentifyToken(lineNo, m);
                queue.add(token);
            }
    }
    protected String toStringLiteral(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length() - 1;
        for (int i = 1; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < len) {
                int c2 = s.charAt(i + 1);
                if (c2 == '"' || c2 == '\\')
                    c = s.charAt(++i);
                else if (c2 == 'n') {
                    ++i;
                    c = '\n';
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }




}
