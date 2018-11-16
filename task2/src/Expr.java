import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Expr {
    enum Proof {NP, AX, MP, AS}

    Oper oper;
    Proof proof;

    int first, second;

    private Expr left, right;

    private Set<String> vars = new HashSet<>();
    private String var;

    int num;
    private Integer hashCode = null;

    public Expr(Oper oper, Expr left, Expr right) {
        this.oper = oper;
        this.proof = Proof.NP;
        this.left = left;
        this.right = right;
    }

    public Expr(Oper oper, String s, Expr left, Expr right) {
        this.oper = oper;
        this.var = s;
        this.proof = Proof.NP;
        this.left = left;
        this.right = right;
    }

    public String toPrefixString() {
        switch (oper) {
            case NONE:
                return var;
            case NOT:
                return "(!" + left.toPrefixString() + ")";
            default:
                return "(" + oper.toString() + "," + left.toPrefixString() + "," + right.toPrefixString() + ")";
        }
    }

    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        Expr e = (Expr) o;
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

    public int hashCode() {
        if (hashCode != null) {
            return hashCode;
        }
        if (oper == Oper.NONE) {
            hashCode = Objects.hash("variable", var);
        } else if (oper == Oper.NOT) {
            hashCode = Objects.hash("!", Integer.toString(left.hashCode()));
        } else {
            hashCode = Objects.hash(oper.toString(), Integer.toString(left.hashCode()), Integer.toString(right.hashCode()));
        }
        return hashCode;
    }

    public String toString() {
        if (var == null) {
            if (oper == Oper.NOT) {
                var = "!" + left.toString();
            } else {
                var = "(" + left.toString() + oper.toString() + right.toString() + ")";
            }
        }
        return var;
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }
}

