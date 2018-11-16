import java.util.*;

public class ProofChecker {
    private List<Expr> axiom = new ArrayList<>();
    private HashMap<Integer, Integer> assumps = new HashMap<>();
    //private List<Integer> MPFirst = new ArrayList<>();
    //private List<Integer> MPSecond = new ArrayList<>();
    //private HashMap<Expr, Integer> MpExpr = new HashMap<>();
    private HashMap<Integer, Integer> exprs = new HashMap<>();
    private HashMap<Integer, List<AbstractMap.SimpleEntry<Integer, Integer>>> mbMp = new HashMap<>();
    private int countAssumps;

    public ProofChecker() {
        Parser parser = new Parser();
        countAssumps = 0;
        try {
            //axiom.add(new Expr(TypeExpr.EXPR, Oper.IMPL, new Expr(TypeExpr.NEG, Oper.NONE, "A", null, null), new Expr(TypeExpr.EXPR, Oper.IMPL, new Expr(TypeExpr.NEG, Oper.NONE, "B", null, null), new Expr(TypeExpr.NEG, Oper.NONE, "A", null, null))));
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
        if (a != null) {
            countAssumps++;
            assumps.put(a.hashCode(), countAssumps);
        }
    }

    public void check(Expr expr) {
        checkAssump(expr);
        if (expr.proof == Expr.Proof.NP) {
            checkAxioms(expr);
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
        HashMap<Integer, Integer> vars = new HashMap<>();
        ArrayDeque<Expr> e = new ArrayDeque<>();
        ArrayDeque<Expr> a = new ArrayDeque<>();
        e.add(expr);
        a.add(ax);
        while (!a.isEmpty() && !e.isEmpty()) {
            Expr currentE = e.pollFirst();
            Expr currentA = a.pollFirst();
            if (currentA.oper == Oper.NONE) {
                if (vars.containsKey(currentA.hashCode())) {
                    //System.out.println(currentE.hash());
                    if (!vars.get(currentA.hashCode()).equals(currentE.hashCode())) {
                        return;
                    }
                } else {
                    vars.put(currentA.hashCode(), currentE.hashCode());
                }
            } else {
                if (currentA.oper == currentE.oper) {
                    e.add(currentE.getLeft());
                    a.add(currentA.getLeft());
                    if (currentA.oper != Oper.NOT) {
                        e.add(currentE.getRight());
                        a.add(currentA.getRight());
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
        Integer pos = assumps.get(expr.hashCode());
        if (pos != null) {
            expr.first = pos;
            expr.proof = Expr.Proof.AS;
        }
    }

    private void checkMP(Expr expr) {
        List<AbstractMap.SimpleEntry<Integer, Integer>> i = mbMp.get(expr.hashCode());
        if (i != null) {
            for (AbstractMap.SimpleEntry<Integer, Integer> e : i) {
                Integer a = exprs.get(e.getKey());
                if (a != null) {
                    expr.first = e.getKey();
                    expr.second = e.getValue();
                    expr.proof = Expr.Proof.MP;
                    return;
                }
            }
        }

    }

    public void addMP(Expr expr) {
        //expr.num = exprs.size() + 1;
        /*for (Expr a : exprs) {
            if (a.oper == Oper.IMPL) {
                if (a.getLeft().hash().equals(hash)) {
                    MpExpr.add(a.getRight().hash());
                    MPFirst.add(expr.num);
                    MPSecond.add(a.num);
                }
            }
        }
        if (expr.oper == Oper.IMPL) {
            for (Expr a: exprs) {
                if (expr.getLeft().hash().equals(a.hash())) {
                    MPSecond.add(expr.num);
                    MPFirst.add(a.num);
                    MpExpr.add(expr.getRight().hash());
                }
            }
        }
        exprs.add(expr);*/
        exprs.put(expr.hashCode(), expr.num);
        if (expr.oper == Oper.IMPL) {
            if (mbMp.containsKey(expr.getRight().hashCode())) {
                mbMp.get(expr.getRight().hashCode()).add(new AbstractMap.SimpleEntry<>(expr.getLeft().hashCode(), expr.hashCode()));
            } else {
                List<AbstractMap.SimpleEntry<Integer, Integer>> temp = new ArrayList<>();
                temp.add(new AbstractMap.SimpleEntry<>(expr.getLeft().hashCode(), expr.hashCode()));
                mbMp.put(expr.getRight().hashCode(), temp);
            }
        }
    }
}

