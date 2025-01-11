package EasyLanguage.exceptions;
import EasyLanguage.ast.ASTNode;

public class EvalException extends RuntimeException {
    public EvalException(String m) { super(m); }
    public EvalException(String m, ASTNode t) {
        super(m + " " + t.location());
    }
}
