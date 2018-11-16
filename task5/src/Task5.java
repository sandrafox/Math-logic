import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Task5 {
    BufferedReader reader = Files.newBufferedReader(Paths.get("input.txt"), Charset.forName("UTF-8"));
    BufferedWriter writer = Files.newBufferedWriter(Paths.get("output.txt"));
    static long time;
    private Expr expression;
    private List<String> numberOfVars = new ArrayList<>();
    private int number;
    //private List<Set<String>> listOfVariables = new ArrayList<>();
    private List<List<Integer>> tree = new ArrayList<>();
    //private List<Integer> roots = new ArrayList<>();
    private Set<String> variables;
    //private String line;
    private List<Integer> masks = new ArrayList<>();

    public Task5() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        time = System.currentTimeMillis();
        Task5 task = new Task5();
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
        try {
            Parser parser = new Parser();
            expression = parser.parse(deleteWhitespaces(reader.readLine()));
            variables = expression.getVariables();
            System.out.println(System.currentTimeMillis() - time);
            number = 0;
            for (String v : variables) {
                numberOfVars.add(v);
            }
            if (!buildTree(expression)) {
                writer.write("Формула общезначима");
                writer.close();
                reader.close();
                return;
            }
            generateHeiting();
            writer.close();
            reader.close();
        } catch (IOException | ParserException e) {
        }

    }

    private Set<Integer> getChildren(int v) {
        Set<Integer> result = new HashSet<>();
        result.add(v);
        for (Integer u : tree.get(v)) {
            if (u != v) {
                result.addAll(getChildren(u));
            }
        }
        return result;
    }

    private boolean isEqual(Set<Integer> a, Set<Integer> b) {
        if (a.size() != b.size()) {
            return false;
        }
        for (int i : a) {
            if (!b.contains(i)) {
                return false;
            }
        }
        return true;
    }

    private void generateHeiting() throws IOException {
        List<Set<Integer>> basic = new ArrayList<>();
        basic.add(new HashSet<>());
        for (int i = 0; i < number; i++) {
            basic.add(getChildren(i));
        }
        List<Set<Integer>> H1 = new ArrayList<>();
        for (int i = 0; i < (1 << basic.size()); i++) {
            Set<Integer> temp = new HashSet<>();
            for (int j = 0; j < basic.size(); j++) {
                if ((i & (1 << j)) != 0) {
                    temp.addAll(basic.get(j));
                }
            }
            H1.add(temp);
        }
        List<Set<Integer>> H = new ArrayList<>();
        for (Set<Integer> h1 : H1) {
            boolean add = true;
            for (Set<Integer> h : H) {
                add &= !isEqual(h1, h);
            }
            if (add) {
                H.add(h1);
            }
        }
        Map<String, Integer> vars = new HashMap<>();
        for (String v : variables) {
            Set<Integer> temp = new HashSet<>();
            for (int i = 0; i < number; i++) {
                if ((masks.get(i) & (1 << numberOfVars.indexOf(v))) != 0) {
                    temp.add(i);
                }
            }
            int world = -1;
            for (int i = 0; i < H.size(); i++) {
                if (temp.containsAll(H.get(i))) {
                    world = world == -1 ? i : (H.get(world).size() < H.get(i).size() ? i : world);
                }
            }
            vars.put(v, world);
        }
        writer.write("" + H.size());
        writer.newLine();
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < H.size(); i++) {
            graph.add(new ArrayList<>());
        }
        for (int i = 0; i < H.size(); i++) {
            for (int j = 0; j < H.size(); j++) {
                if (H.get(j).containsAll(H.get(i)) && !isEqual(H.get(i), H.get(j))) {
                    graph.get(i).add(j);
                }
            }
            graph.get(i).add(i);
        }
        for (List<Integer> l: graph) {
            for (int i : l) {
                writer.write((i + 1) + " ");
            }
            writer.newLine();
        }
        boolean first = true;
        for (String v : variables) {
            if (!first) {
                writer.write(",");
            } else {
                first = false;
            }
            writer.write(v + "=" + (vars.get(v) + 1));
        }
    }

    private boolean checkingNotProofForModel(Expr e) {
        for (int i = 0; i < number; i++) {
            if (!checkingProofForWorld(e, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkingProofForWorld(Expr e, int world) {
        boolean res = false;
        switch (e.oper) {
            case NONE:
                res = ((masks.get(world) & (1 << numberOfVars.indexOf(e.toString()))) != 0);
                break;
            case NOT:
                res = getChildren(world).stream().noneMatch(w -> checkingProofForWorld(e.getLeft(), w));
                break;
            case AND:
                res = checkingProofForWorld(e.getLeft(), world) && checkingProofForWorld(e.getRight(), world);
                break;
            case OR:
                res = checkingProofForWorld(e.getRight(), world) || checkingProofForWorld(e.getLeft(), world);
                break;
            case IMPL:
                res = getChildren(world).stream().allMatch((Integer w) ->
                        (!checkingProofForWorld(e.getLeft(), w) | checkingProofForWorld(e.getRight(), w)));
                break;
        }
        return res;
    }

    /*private boolean checkingNotProofForWorld(Expr e, int world) {
        switch (e.oper) {
            case NONE:
                return tree.get(world).stream().anyMatch(w -> !listOfVariables.get(w).contains(e.toString()));
            case NOT:
                return tree.get(world).stream().anyMatch(w -> listOfVariables.get(w).contains(e.toString()));
            case AND:
                return checkingNotProofForWorld(e.getLeft(), world) || checkingNotProofForWorld(e.getRight(), world);
            case OR:
                return checkingNotProofForWorld(e.getLeft(), world) && checkingNotProofForWorld(e.getRight(), world);
            case IMPL:
                return tree.get(world).stream().anyMatch()
        }
    }*/

    /*private String buildTree() throws IOException{
        List<Integer> indents = new ArrayList<>();
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                continue;
            }
            int indent = 0;
            while (Character.isWhitespace(line.charAt(indent))) {
                indent++;
            }
            int parent = -1;
            for (int i = number - 1; i >= 0; i--) {
                if (indents.get(i) + 1 == indent) {
                    parent = i;
                    break;
                }
            }
            if (parent == -1) {
                roots.add(number);
            } else {
                tree.get(parent).add(number);
            }
            tree.add(new ArrayList<>());
            tree.get(number).add(number);
            indents.add(indent);
            Set<String> temp = new HashSet<>();
            for (String v : line.substring(indent + 1).replace(',', ' ').split("\\s+")) {
                if (v.length() > 0) {
                    temp.add(v);
                }
            }
            listOfVariables.add(temp);
            if (parent != -1 && !listOfVariables.get(number).containsAll(listOfVariables.get(parent))) {
                return "bad";
            }
            number++;
        }
        return "good";
    }*/

    private boolean buildTree(Expr e) {
        int n = variables.size();
        tree.add(new ArrayList<>());
        tree.get(0).add(0);
        number = 1;
        for (int i = 0; i < (1 << n); i++) {
            //listOfVariables.add(getSetOfVars(i));
            masks.add(i);
            if (buildTreeInc(e)) {
                return true;
            }
            //listOfVariables.remove(0);
            masks.remove(0);
        }
        return false;
    }

    private Set<String> getSetOfVars(int mask) {
        Set<String> res = new HashSet<>();
        number = 1;
        for (int i = 0; i < variables.size(); i++) {
            if ((mask & (1 << i)) != 0) {
                res.add(numberOfVars.get(i));
            }
        }
        return res;
    }

    private boolean buildTreeInc(Expr e) {
        if (checkingNotProofForModel(e)) {
            return true;
        }
        if (number > 5) {
            return false;
        }
        for (int i = 0; i < number; i++) {
            int prevMask = masks.get(i);
            for (int j = prevMask; j < (1 << variables.size()); j++) {
                if ((j & prevMask) == prevMask) {
                    masks.add(j);
                    tree.add(new ArrayList<>());
                    tree.get(i).add(number);
                    tree.get(number).add(number);
                    number++;
                    if (buildTreeInc(e)) {
                        return true;
                    }
                    number--;
                    tree.get(i).remove(tree.get(i).size() - 1);
                    tree.remove(number);
                    masks.remove(number);
                }
            }
        }
        return false;
    }
}
