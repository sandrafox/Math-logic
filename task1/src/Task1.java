import java.io.*;

public class Task1 {
    public static void main (String[] args) {
        Task1 task = new Task1();
        task.solve();
    }

    public void solve() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt"), "UTF-8"))) {
            try (Writer writer = new OutputStreamWriter(new FileOutputStream("output.txt"), "UTF-8")) {
                Parser parser = new Parser();
                String line;
                int num = 0;
                ProofChecker proofChecker = new ProofChecker();
                while ((line = reader.readLine()) != null) {
                    if (num == 0 && line.contains("|-")) {
                        int start = 0;
                        for (int i = 0; i < line.length(); ++i) {
                            if (line.charAt(i) == ',') {
                                proofChecker.addAssump(parser.parse(line.substring(start, i)));
                                start = i + 1;
                            }
                            if (i < line.length() - 1 && line.charAt(i) == '|' && line.charAt(i + 1) == '-') {
                                proofChecker.addAssump(parser.parse(line.substring(start, i)));
                                start = i + 2;
                                i = line.length();
                            }
                        }
                        Expr b = parser.parse(line.substring(start));
                        num++;
                        continue;
                    }
                    Expr res = parser.parse(line);
                    proofChecker.check(res);
                    writer.write("(" + num + ") " + res.var + " (");
                    if (res.proof == Expr.Proof.AX) {
                        writer.write("Сх. акс. " + res.first);
                    } else if (res.proof == Expr.Proof.AS) {
                        writer.write("Предп. " + res.first);
                    } else if (res.proof == Expr.Proof.MP) {
                        writer.write("M.P. " + res.first + " " + res.second);
                    } else {
                        writer.write("Не доказано");
                    }
                    writer.write(")\n");
                    num++;
                }
            } catch (IOException | ParserException e) {

            }
        } catch (IOException e) {

        }
    }
}
