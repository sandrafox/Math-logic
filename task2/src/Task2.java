import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Task2 {
    static long time;
    private Expr assump;
    private HashMap<Integer, Expr> exprs = new HashMap<>();
    public static void main (String[] args) {
        time = System.currentTimeMillis();
        Task2 task = new Task2();
        task.solve();
    }

    private String deleteWhitespaces(String s) {
        StringBuilder e = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (!Character.isWhitespace(s.charAt(i)) && s.charAt(i) != '\t' && s.charAt(i) != '\r') e.append(s.charAt(i));
        }
        return e.toString();
    }

    public void solve() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt"), "UTF-8"))) {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"))) {
                Parser parser = new Parser();
                String line;
                int num = 1;
                ProofChecker proofChecker = new ProofChecker();
                line = deleteWhitespaces(reader.readLine());
                String[] header = line.split("\\|-");
                String[] hypos = header[0].split(",");
                if (hypos.length > 1) {
                    Expr hypo = parser.parse(hypos[0]);
                    writer.write(hypo.toString());
                    proofChecker.addAssump(hypo);
                    for (int i = 1; i < hypos.length - 1; i++) {
                        hypo = parser.parse(hypos[i]);
                        writer.write("," + hypo.toString());
                        proofChecker.addAssump(hypo);
                    }
                }
                writer.write("|-");
                assump = parser.parse(hypos[hypos.length - 1]);
                writer.write((new Expr(Oper.IMPL, assump, parser.parse(header[1]))).toString());
                writer.newLine();
                while ((line = reader.readLine()) != null) {
                    line = deleteWhitespaces(line);
                    if (line.equals("")) {
                        continue;
                    }
                    Expr res = parser.parse(line);
                    res.num = num;
                    if (res.hashCode() == assump.hashCode()) {
                        Expr e1 = new Expr(Oper.IMPL, assump, assump);
                        Expr e2 = new Expr(Oper.IMPL, assump, e1);
                        writer.write(e2.toString());
                        writer.newLine();
                        Expr e3 = new Expr(Oper.IMPL, assump, new Expr(Oper.IMPL, e1, assump));
                        writer.write(e3.toString());
                        writer.newLine();
                        Expr e4 = new Expr(Oper.IMPL, e3, e1);
                        Expr e5 = new Expr(Oper.IMPL, e2, e4);
                        writer.write(e5.toString());
                        writer.newLine();
                        writer.write(e4.toString());
                        writer.newLine();
                        writer.write(e1.toString());
                        writer.newLine();
                        proofChecker.addMP(res);
                    } else {
                        proofChecker.check(res);
                        //writer.write("(" + num + ") " + res.toString() + " (");
                        if (res.proof == Expr.Proof.AX || res.proof == Expr.Proof.AS) {
                            //writer.write("Сх. акс. " + res.first);
                            writer.write(res.toString());
                            writer.newLine();
                            Expr e = new Expr(Oper.IMPL, assump, res);
                            writer.write((new Expr(Oper.IMPL, res, e)).toString());
                            writer.newLine();
                            writer.write(e.toString());
                            writer.newLine();
                        } else if (res.proof == Expr.Proof.MP) {
                            //writer.write("M.P. " + res.second + ", " + res.first);
                            Expr e1 = new Expr(Oper.IMPL, new Expr(Oper.IMPL, assump, exprs.get(res.second)), new Expr(Oper.IMPL, assump, res));
                            writer.write((new Expr(Oper.IMPL, new Expr(Oper.IMPL, assump, exprs.get(res.first)), e1)).toString());
                            writer.newLine();
                            writer.write(e1.toString());
                            writer.newLine();
                            writer.write((new Expr(Oper.IMPL, assump, res)).toString());
                            writer.newLine();
                        }
                    }
                    num++;
                    exprs.put(res.hashCode(), res);
                }
                System.out.println(System.currentTimeMillis() - time);
            } catch (IOException | ParserException e) {

            }
        } catch (IOException e) {

        }
    }
}

