import java.util.HashSet;
import java.util.Set;

public class Expr {
    enum TypeExpr {EXPR, DISJ, CONJ, NEG}
    enum Oper {NONE, NOT, AND, OR, IMPL}
    enum Proof {NP, AX, MP, AS}

    TypeExpr type;
    Oper oper;
    Proof proof;

    int first, second;

    Expr left, right;

    private Set<String> vars = new HashSet<>();
    String var;

    int num;

    public Expr(TypeExpr t, Oper oper) {
        this.type = t;
        this.oper = oper;
        this.proof = Proof.NP;
    }

    public Expr(TypeExpr t, Oper oper, String s) {
        this.type = t;
        this.oper = oper;
        this.var = s;
        this.proof = Proof.NP;
    }

    public boolean equals(Expr e) {
        if (this.oper == e.oper) {
            if (this.oper == Oper.NONE) {
                return this.var.equals(e.var);
            }
            if (this.oper == Oper.NOT) {
                return this.left.equals(e.left);
            }
            return this.left.equals(e.left) && this.right.equals(e.right);
        } else {
            return false;
        }
    }
}
