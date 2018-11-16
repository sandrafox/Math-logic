import java.util.*;

public class ProofChecker {
    List<Expr> axiom = new ArrayList<>();
    List<Expr> assump = new ArrayList<>();
    List<Integer> MPFirst = new ArrayList<>();
    List<Integer> MPSecond = new ArrayList<>();
    List<Expr> MpExpr = new ArrayList<>();
    List<Expr> exprs = new ArrayList<>();

    public ProofChecker() {
        Parser parser = new Parser();
        try {
            for (String a : Axioms.axioms) {
                axiom.add(parser.parse(a));
            }
            int i = 1;
            for (Expr a : axiom) {
                a.num = i;
                i++;
            }
        } catch (ParserException e){}
    }

    public void addAssump(Expr a) {
        assump.add(a);
    }

    public void check(Expr expr) {
        checkAxioms(expr);
        if (expr.proof == Expr.Proof.NP) {
            checkAssump(expr);
        }
        if (expr.proof == Expr.Proof.NP) {
            checkMP(expr);
        }
        addMP(expr);
    }

    private void checkAxioms(Expr expr) {
        int i = 0;
        while (i < axiom.size() && expr.proof == Expr.Proof.NP) {
            checkAxiom(expr, axiom.get(i));
            i++;
        }
    }

    private void checkAxiom(Expr expr, Expr ax) {
        Map<String, String> vars = new HashMap<>();
        ArrayDeque<Expr> e = new ArrayDeque<>();
        ArrayDeque<Expr> a = new ArrayDeque<>();
        e.add(expr);
        a.add(ax);
        while (!a.isEmpty() && !e.isEmpty()) {
            Expr currentE = e.pollFirst();
            Expr currentA = a.pollFirst();
            if (currentA.oper == Expr.Oper.NONE) {
                if (vars.containsKey(currentA.var)) {
                    if (!vars.get(currentA.var).equals(currentE.var)) {
                        return;
                    }
                } else {
                    vars.put(currentA.var, currentE.var);
                }
            } else {
                if (currentA.oper == currentE.oper) {
                    e.add(currentE.left);
                    a.add(currentA.left);
                    if (currentA.oper != Expr.Oper.NOT) {
                        e.add(currentE.right);
                        a.add(currentA.right);
                    }
                } else {
                    return;
                }
            }
        }
        expr.proof = Expr.Proof.AX;
        expr.first = ax.num;
    }

    private void checkAssump(Expr expr) {
        for (Expr a : assump) {
            if (a.equals(expr)) {
                expr.proof = Expr.Proof.AS;
                expr.first = a.num;
                break;
            }
        }
    }

    private void checkMP(Expr expr) {
        int i = 0;
        for (Expr a : MpExpr) {
            if (a.equals(expr)) {
                a.proof = Expr.Proof.MP;
                a.first = MPFirst.get(i);
                a.second = MPSecond.get(i);
                break;
            }
            i++;
        }
    }

    private void addMP(Expr expr) {
        expr.num = exprs.size() + 1;
        for (Expr a : exprs) {
            if (a.oper == Expr.Oper.IMPL) {
                if (a.left.equals(expr)) {
                    MpExpr.add(a);
                    MPFirst.add(expr.num);
                    MPSecond.add(a.num);
                }
            }
        }
        if (expr.oper == Expr.Oper.IMPL) {
            for (Expr a: exprs) {
                if (expr.left.equals(a)) {
                    MPSecond.add(expr.num);
                    MPFirst.add(a.num);
                    MpExpr.add(expr);
                }
            }
        }
    }
}
