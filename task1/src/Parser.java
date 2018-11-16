public class Parser {
    String expression;
    int i;
    int length;

    public Expr parse(String s) throws ParserException {
        expression = deleteWhiteSpaces(s);
        i = 0;
        length = expression.length();
        Expr expr = parseExpr();
        if (current() != '$') {
            throw new ParserException();
        }
        return expr;
    }

    private Expr parseExpr() throws ParserException {
        Expr exprLeft = parseDisj();
        while (current() == '-') {
            i++;
            if (current() == '>') {
                i++;
                Expr res = new Expr(Expr.TypeExpr.EXPR, Expr.Oper.IMPL);
                res.left = exprLeft;
                res.right = parseDisj();
                res.var = "(" + res.left.var + "->" + res.right.var + ")";
                exprLeft = res;
            } else {
                throw new ParserException();
            }
        }
        return exprLeft;
    }

    private Expr parseDisj() throws ParserException {
        Expr exprLeft = parseConj();
        while (current() == '|') {
            i++;
            Expr res = new Expr(Expr.TypeExpr.DISJ, Expr.Oper.OR);
            res.left = exprLeft;
            res.right = parseConj();
            res.var = "(" + res.left.var + "|" + res.right.var + ")";
            exprLeft = res;
        }
        return exprLeft;
    }

    private Expr parseConj() throws ParserException {
        Expr exprLeft = parseNeg();
        while (current() == '&') {
            i++;
            Expr res = new Expr(Expr.TypeExpr.CONJ, Expr.Oper.AND);
            res.left = exprLeft;
            res.right = parseNeg();
            exprLeft = res;
            exprLeft.var = "(" + exprLeft.left.var + "&" + exprLeft.right.var + ")";
        }
        return exprLeft;
    }

    private Expr parseNeg() throws ParserException {
        char a = current();
        if (Character.isUpperCase(a)) {
            String var = "";
            while (Character.isUpperCase(a) || Character.isDigit(a)) {
                var += a;
                i++;
                a = current();
            }
            Expr res = new Expr(Expr.TypeExpr.NEG, Expr.Oper.NONE, var);
            return res;
        }
        if (a == '(') {
            i++;
            Expr expr = parseExpr();
            if (current() == ')') {
                return expr;
            } else {
                throw new ParserException();
            }
        }
        if (a == '!') {
            i++;
            Expr expr = new Expr(Expr.TypeExpr.NEG, Expr.Oper.NOT);
            expr.left = parseExpr();
            return expr;
        }
        throw new ParserException();
    }

    private char current() {
        if (i == length) {
            return '$';
        }
        return expression.charAt(i);
    }

    private String deleteWhiteSpaces(String s) {                                                                                                                   StringBuilder e = new StringBuilder("");
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != ' ') e.append(s.charAt(i));
        }
        return e.toString();
    }
}
