import java.io.*;

public class Parser {
    private String expression;
    private int i;
    private int length;

    public static void main(String[] args) {
        Parser parser = new Parser();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt"), "UTF-8"))) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream("output.txt"), "UTF-8")) {
                String line;
                String e = "";
                while ((line = reader.readLine()) != null) {
                    e += line;
                }
                writer.write(parser.parse(deleteWhitespaces(e)).toPrefixString());
            } catch (IOException | ParserException e) {}
        } catch (IOException e) {}
    }

    private static String deleteWhitespaces(String s) {
        StringBuilder e = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i)) && s.charAt(i) != '\t' && s.charAt(i) != '\r' && s.charAt(i) != '\n') e.append(s.charAt(i));
        }
        return e.toString();
    }

    public Expr parse(String s) throws ParserException {
        expression = s;
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
                exprLeft = new Expr(Oper.IMPL, exprLeft, parseExpr());
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
            Expr res = new Expr(Oper.OR, exprLeft, parseConj());
            exprLeft = res;
        }
        return exprLeft;
    }

    private Expr parseConj() throws ParserException {
        Expr exprLeft = parseNeg();
        while (current() == '&') {
            i++;
            Expr res = new Expr(Oper.AND, exprLeft, parseNeg());
            exprLeft = res;
        }
        return exprLeft;
    }

    private Expr parseNeg() throws ParserException {
        char a = current();
        if (Character.isLetter(a)) {
            int start = i;
            while (Character.isLetter(a) || Character.isDigit(a)) {
                i++;
                a = current();
            }
            return new Expr(Oper.NONE, expression.substring(start, i), null, null);
        }
        if (a == '(') {
            i++;
            Expr expr = parseExpr();
            if (current() == ')') {
                i++;
                return expr;
            } else {
                throw new ParserException();
            }
        }
        if (a == '!') {
            i++;
            return new Expr(Oper.NOT, parseNeg(), null);
        }
        throw new ParserException();
    }

    private char current() {
        if (i == length) {
            return '$';
        }
        return expression.charAt(i);
    }
}

