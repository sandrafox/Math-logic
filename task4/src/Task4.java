import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Task4 {
    static long time;
    private ArrayList<ArrayList<Integer>> g = new ArrayList<>(), gtr = new ArrayList<>(), rtr = new ArrayList<>(), r = new ArrayList<>(), sum = new ArrayList<>(), mult = new ArrayList<>(), impl = new ArrayList<>();
    private ArrayList<Integer> neg = new ArrayList<>();
    private ArrayList<Boolean> used = new ArrayList<>();

    public static void main (String[] args) {
        time = System.currentTimeMillis();
        Task4 task = new Task4();
        task.solve();
    }

    public void solve() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("input.txt"), "UTF-8"))) {
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"))) {
                int n = Integer.parseInt(reader.readLine());
                for (int i = 0; i < n; i++) {
                    g.add(new ArrayList<>());
                    r.add(new ArrayList<>());
                    sum.add(new ArrayList<>());
                    mult.add(new ArrayList<>());
                    impl.add(new ArrayList<>());
                    neg.add(-1);
                    used.add(false);
                    gtr.add(new ArrayList<>());
                    rtr.add(new ArrayList<>());
                    for (int j = 0; j < n; j++) {
                        sum.get(i).add(-1);
                        mult.get(i).add(-1);
                        impl.get(i).add(-1);
                    }
                }
                for (int i = 0; i < n; i++) {
                    String[] s = reader.readLine().split(" ");
                    for (String c : s) {
                        int num = Integer.parseInt(c) - 1;
                        g.get(i).add(num);
                        r.get(num).add(i);
                    }
                }
                int min = 0, max = 0;
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        used.set(j, false);
                    }
                    dfs(i);
                    for (int j = 0; j < n; j++) {
                        if (used.get(j)) {
                            gtr.get(i).add(j);
                            rtr.get(j).add(i);
                        }
                    }
                    if (gtr.get(i).size() == n) {
                        min = i;
                    }
                    if (gtr.get(i).size() == 1) {
                        max = i;
                    }
                }
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        ArrayList<Integer> temp = new ArrayList<>();
                        int bound = -1;
                        for (int k = 0; k < n; k++) {
                            if (gtr.get(i).contains(k) && gtr.get(j).contains(k)) {
                                if (bound == -1 || gtr.get(k).contains(bound)) {
                                    bound = k;
                                } else if (!gtr.get(bound).contains(k)) {
                                    temp.add(k);
                                }
                            }
                        }
                        if (bound == -1) {
                            writer.write("Операция '+' не определена: " + (i + 1) + "+" + (j + 1));
                            writer.close();
                            reader.close();
                            System.out.println(System.currentTimeMillis() - time);
                            return;
                        }
                        for (int k : temp) {
                            if (!gtr.get(bound).contains(k)) {
                                writer.write("Операция '+' не определена: " + (i + 1) + "+" + (j + 1));
                                writer.close();
                                reader.close();
                                System.out.println(System.currentTimeMillis() - time);
                                return;
                            }
                        }
                        sum.get(i).set(j, bound);
                    }
                }
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        ArrayList<Integer> temp = new ArrayList<>();
                        int bound = -1;
                        for (int k = 0; k < n; k++) {
                            if (gtr.get(k).contains(i) && gtr.get(k).contains(j)) {
                                if (bound == -1 || gtr.get(bound).contains(k)) {
                                    bound = k;
                                } else if (!gtr.get(k).contains(bound)) {
                                    temp.add(k);
                                }
                            }
                        }
                        if (bound == -1) {
                            writer.write("Операция '*' не определена: " + (i + 1) + "*" + (j + 1));
                            writer.close();
                            reader.close();
                            System.out.println(System.currentTimeMillis() - time);
                            return;
                        }
                        for (int k : temp) {
                            if (!gtr.get(k).contains(bound)) {
                                writer.write("Операция '*' не определена: " + (i + 1) + "*" + (j + 1));
                                writer.close();
                                reader.close();
                                System.out.println(System.currentTimeMillis() - time);
                                return;
                            }
                        }
                        mult.get(i).set(j, bound);
                    }
                }
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        for (int k = 0; k < n; k++) {
                            int a1 = mult.get(i).get(sum.get(j).get(k)), a2 = sum.get(mult.get(i).get(j)).get(mult.get(i).get(k));
                            if (a1 != a2) {
                                writer.write("Нарушается дистрибутивность: " + (i + 1) + "*(" + (j + 1) + "+" + (k + 1) + ")");
                                reader.close();
                                writer.close();
                                System.out.println(System.currentTimeMillis() - time);
                                return;
                            }
                        }
                    }
                }
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        ArrayList<Integer> temp = new ArrayList<>();
                        int bound = -1;
                        for (int k = 0; k < n; k++) {
                            if (gtr.get(mult.get(i).get(k)).contains(j)) {
                                if (bound == -1 || gtr.get(bound).contains(k)) {
                                    bound = k;
                                } else if (!gtr.get(k).contains(bound)) {
                                    temp.add(k);
                                }
                            }
                        }
                        if (bound == -1) {
                            writer.write("Операция '->' не определена: " + (i + 1) + "->" + (j + 1));
                            writer.close();
                            reader.close();
                            System.out.println(System.currentTimeMillis() - time);
                            return;
                        }
                        for (int k : temp) {
                            if (!gtr.get(k).contains(bound)) {
                                writer.write("Операция '->' не определена: " + (i + 1) + "->" + (j + 1));
                                writer.close();
                                reader.close();
                                System.out.println(System.currentTimeMillis() - time);
                                return;
                            }
                        }
                        impl.get(i).set(j, bound);
                    }
                }
                for (int i = 0; i < n; i++) {
                    if (sum.get(i).get(impl.get(i).get(min)) != max) {
                        writer.write("Не булева алгебра: " + (i + 1) + "+~" + (i + 1));
                        writer.close();
                        reader.close();
                        System.out.println(System.currentTimeMillis() - time);
                        return;
                    }
                }
                writer.write("Булева алгебра");
                writer.close();
                reader.close();
                System.out.println(System.currentTimeMillis() - time);
            } catch (IOException e) {
            }
        } catch (IOException e) {

        }
    }

    private void dfs(int v) {
        if (used.get(v)) {
            return;
        }
        used.set(v, true);
        for (int u : g.get(v)) {
            if (!used.get(u)) {
                dfs(u);
            }
        }
    }
}