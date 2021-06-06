import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import java.util.*;

public class StateMachine {

    private int n = 0;
    private int startState = 0;
    private ArrayList<String> alphabet = new ArrayList<>();
    private ArrayList<Boolean> isTerminal = new ArrayList<>();
    private ArrayList<ListMultimap<String, Integer>> sigma = new ArrayList<>();

    StateMachine() { }

    StateMachine(int numberOfStates, int startState, ArrayList<String> alphabet) {
        this.setNumberOfStates(numberOfStates);
        this.startState = startState;
        this.alphabet = alphabet;
    }

    public void setNumberOfStates(int numberOfStates) {
        this.n = numberOfStates;
        for (int j = 0; j < n; j++) {
            this.sigma.add(ArrayListMultimap.create());
        }
    }

    public void setStartState(int startState) {
        this.startState = startState;
    }

    public void setAlphabet(ArrayList<String> alphabet) {
        this.alphabet = alphabet;
    }

    public void addTransition(int fromState, String word, int toState) {
        this.sigma.get(fromState).put(word, toState);
    }

    public void setTerminalStates(String[] a) {
        for(int i = 0; i < n; i++) {
            this.isTerminal.add(false);
        }
        for(int i = 0; i < a.length; i++) {
            if (i != 0) {
                this.isTerminal.set(Integer.parseInt(a[i]), true);
            }
        }
    }

    public void setTerminalStates(ArrayList<Integer> a) {
        for(int i = 0; i < n; i++) {
            this.isTerminal.add(false);
        }
        for (Integer integer : a) {
            this.isTerminal.set(integer, true);
        }
    }
    public void print() {
        System.out.println("==============================================================");
        System.out.print("Number of states: ");
        System.out.println(this.n);
        System.out.print("SIGMA (edge list): ");
        System.out.println(this.sigma);
        System.out.print("Terminal states: ");
        System.out.println(this.isTerminal);
        System.out.print("Alphabet: ");
        System.out.println(this.alphabet);
        System.out.println("==============================================================");
    }

    public String shortestStringWith(StateMachine other) {
        StateMachine intersectedMachine = this.intersectionWith(other);
        StringBuilder res = new StringBuilder();
        Queue<Integer> queue = new LinkedList<>();
        ArrayList<Boolean> used = new ArrayList<>();
        ArrayList<Integer> parentOfPathItem = new ArrayList<>();
        for (int i = 0; i < intersectedMachine.n; i++) {
            used.add(false);
            parentOfPathItem.add(-2);
        }
        used.set(intersectedMachine.startState, true);
        queue.add(intersectedMachine.startState);
        parentOfPathItem.set(intersectedMachine.startState, -1);

        while (!queue.isEmpty()) {
            Integer v = queue.poll();
            for (String tWord: intersectedMachine.sigma.get(v).keySet()) {
                int to = intersectedMachine.sigma.get(v).get(tWord).get(0);
                if (used.get(to).equals(false)) {
                    used.set(to, true);
                    queue.add(to);
                    parentOfPathItem.set(to, v);
                }
            }
        }
        ArrayList<Integer> terminalStates = new ArrayList<>();
        for (int i = 0; i < intersectedMachine.isTerminal.size(); i++) {
            if (intersectedMachine.isTerminal.get(i).equals(true)) {
                terminalStates.add(i);
            }
        }
        ArrayList<Integer> shortestPath = new ArrayList<>();
        Boolean isUsed = false;
        for (Integer terminalState: terminalStates) {
            if (used.get(terminalState).equals(false)) {
                continue;
            }
            ArrayList<Integer> path = new ArrayList<>();
            for (Integer v = terminalState; v != -1; v = parentOfPathItem.get(v)) {
                path.add(v);
            }
            Collections.reverse(path);
            if (shortestPath.size() > path.size() || isUsed.equals(false)) {
                shortestPath = path;
                isUsed = true;
            }
        }
        if(shortestPath.size() == 0) {
            return "Shortest string that accept this two DFA are not exists :(";
        }

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            for (String word: intersectedMachine.sigma.get(shortestPath.get(i)).keySet()) {
                if (intersectedMachine.sigma.get(shortestPath.get(i)).get(word).get(0).equals(shortestPath.get(i + 1))) {
                    res.append(word);
                }
            }
        }
        return res.toString();
    }

    // Перетин двох автоматів
    public StateMachine intersectionWith(StateMachine other) {
        StateMachine res = new StateMachine();
        res.setNumberOfStates(this.n * other.n);
        res.setAlphabet(this.alphabet);
        ArrayList<Integer> resTerminalStates = new ArrayList<>();

        int countF = 0, countS = 0;
        for (ListMultimap<String, Integer> itemTransitions_1: sigma) {
            for (ListMultimap<String, Integer> itemTransitions_2: other.sigma) {
                for (String word : itemTransitions_1.keySet()) {
                    if (itemTransitions_1.get(word).size() == 0 || itemTransitions_2.get(word).size() == 0) {
                        continue;
                    }
                    Integer from = itemTransitions_1.get(word).get(0);
                    Integer to = itemTransitions_2.get(word).get(0);
                    res.addTransition(countF * other.n + countS, word, from * other.n + to);
                    if (this.isTerminal.get(countF) && other.isTerminal.get(countS)) {
                        resTerminalStates.add(countF * other.n + countS);
                    }
                    if (countF == this.startState && countS == other.startState) {
                        res.setStartState(countF * other.n + countS);
                    }
                }
                countS++;
            }
            countS = 0;
            countF++;
        }
        res.setTerminalStates(resTerminalStates);
        return res;
    }

    // Алгоритм Томпсона для перетворення недетермінованого автомату в детермінований
    public StateMachine toDFAbyNFA() {
        StateMachine dfa = new StateMachine(5 * n, startState, alphabet);
        Queue<HashSet<Integer>> P = new LinkedList<>();
        HashSet<HashSet<Integer>> Q = new HashSet<>();
        HashSet<Integer> startedSet = new HashSet<>();
        HashMap<HashSet<Integer>, Integer> stateMap = new HashMap<>();
        startedSet.add(this.startState);
        stateMap.put(startedSet, 0);
        P.add(startedSet);
        ArrayList<Integer> dfaTerminalStates = new ArrayList<>();

        int countOfStates = 1;
        while (!P.isEmpty()) {
            HashSet<Integer> pd = P.poll();
            for (String word: alphabet) {
                HashSet<Integer> q = new HashSet<>();
                for (Integer p: pd) {
                    q.addAll(this.sigma.get(p).get(word));
                }
                stateMap.put(q, countOfStates);
                if (!dfa.sigma.get(stateMap.get(pd)).get(word).contains(stateMap.get(q))) {
                    dfa.addTransition(stateMap.get(pd), word, stateMap.get(q));
                }
                for (Integer p: q) {
                    if (isTerminal.get(p).equals(true) && !dfaTerminalStates.contains(countOfStates)) {
                        dfaTerminalStates.add(countOfStates);
                    }
                }
                if (!Q.contains(q)) {
                    P.add(q);
                    Q.add(q);
                    countOfStates++;
                }
            }
        }
        dfa.setTerminalStates(dfaTerminalStates);
        return dfa;
    }

    private void log(String title, Object a) {
        System.out.println(title);
        System.out.print('\t');
        System.out.println(a);
    }
}