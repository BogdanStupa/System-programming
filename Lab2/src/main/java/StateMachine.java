import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import javafx.util.Pair;
import java.util.*;

public class StateMachine {

    private int n = 0;
    private int startState = 0;
    private ArrayList<String> alphabet = new ArrayList<>();
    private ArrayList<Boolean> isTerminal = new ArrayList<>();
    private ArrayList<ListMultimap<String, Integer>> sigma = new ArrayList<>();

    StateMachine() { }

    StateMachine(int n, ArrayList<ListMultimap<String, Integer>> sigma, ArrayList<Boolean> isTerminal, int startState,  ArrayList<String> alphabet) {
        this.n = n;
        this.sigma = sigma;
        this.isTerminal = isTerminal;
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

    public boolean isMachineEquivalentTo(StateMachine other) {
        StateMachine minimizedThisMachine = this.minimizeStateMachine();
        StateMachine minimizedOtherMachine = other.minimizeStateMachine();
        return minimizedThisMachine.isMachineIsomorphicTo(minimizedOtherMachine);
    }

    public StateMachine minimizeStateMachine() {
        System.out.println("============ Start minimize state machine ================");

        ArrayList<ListMultimap<String, Integer>> inverseSigma = buildInverseSigma();
        ArrayList<Boolean> reachable = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            reachable.add(false);
        }
        this.buildReachableByDFS(this.startState, reachable);
        ArrayList<ArrayList<Boolean>> marked = buildTable(inverseSigma);
        ArrayList<Integer> component = new ArrayList<>();
        for (int i = 0; i < this.n; i++) {
            component.add(-1);
        }
        int componentsCount = 0;
        for (int i = 0; i < this.n; i++) {
            if (reachable.get(i).equals(true)) {
                if (component.get(i).equals(-1)) {
                    componentsCount++;
                    component.set(i, componentsCount);
                    for (int j = i + 1; j < n; j++ ) {
                        if (marked.get(i).get(j).equals(false)) {
                            component.set(j, componentsCount);
                        }
                    }
                }
            }
        }
        this.log("1) Build inverse sigma (reverse edge list):", inverseSigma);
        this.log("2) Set reachable states:", reachable);
        this.log("3) Build matrix of equivalent state:", marked);
        this.log("4) Splitting the states into equivalence classes:", component);
        System.out.println(component);
        return this.buildDFA(component, reachable);
    }

    public boolean isStateTerminal(int state) {
        if (state > this.n || state < 1) {
            return false;
        }
        return this.isTerminal.get(state - 1);
    }

    public boolean isMachineIsomorphicTo(StateMachine other) {
        if (this.n != other.n) {
            return false;
        }
        ArrayList<Boolean> visited = new ArrayList<>();
        HashMap<Integer, Integer> associations = new HashMap<>();
        int size = Math.max(this.n, other.n);
        for (int i = 0; i < size; i++) {
            visited.add(false);
        }
        return this.checkOnIsomorphism(this.startState, other.startState, other, visited, associations);
    }

    private boolean checkOnIsomorphism(int v, int u, StateMachine otherMachine, ArrayList<Boolean> visited, HashMap<Integer, Integer> associations) {
        visited.set(u, true);
        if (this.isStateTerminal(u) != otherMachine.isStateTerminal(v)) {
            return false;
        }
        associations.put(u, v);
        boolean result = true;
        for (String word: this.sigma.get(u).keySet()) {
            for (Integer stateInThisMachine: this.sigma.get(u).get(word)) {
                for (Integer stateInOtherMachine: otherMachine.sigma.get(u).get(word)) {
                    if (visited.get(stateInThisMachine)) {
                        result &= stateInOtherMachine.equals(associations.get(stateInThisMachine));
                    } else  {
                        result &= this.checkOnIsomorphism(stateInThisMachine, stateInOtherMachine, otherMachine, visited, associations);
                    }
                }
            }
        }
        return result;
    }

    private StateMachine buildDFA(ArrayList<Integer> component, ArrayList<Boolean> reachable) {
        System.out.println("Build minimized state machine");

        ArrayList<ListMultimap<String, Integer>> dfa = new ArrayList<>();
        int numberOfStatesDFA = Collections.max(component);
        HashMap<Integer, Integer> mapFromThisToComponentMachine = new HashMap<>();

        for (int i = 0; i < numberOfStatesDFA; i++) {
            dfa.add(ArrayListMultimap.create());
        }
        for (int i = 0; i < this.n; i++) {
            if (reachable.get(i).equals(true)) {
                mapFromThisToComponentMachine.put(i, component.get(i) - 1);
            }
        }
        mapFromThisToComponentMachine.forEach((key, value) -> {
            for (String word: sigma.get(key).keySet()) {
                for (Integer state: sigma.get(key).get(word)) {
                    if (!dfa.get(value).get(word).contains(mapFromThisToComponentMachine.get(state))) {
                        dfa.get(value).put(word, component.get(state) - 1);
                    }
                }
            }
        });

        ArrayList<Boolean> isTerminalDFA = new ArrayList<>();
        for (int i = 0; i < numberOfStatesDFA; i++) {
            isTerminalDFA.add(false);
        }
        for (int i = 0; i < this.n; i++) {
            if (reachable.get(i).equals(true)) {
                isTerminalDFA.set(mapFromThisToComponentMachine.get(i), isTerminal.get(i));
            }
        }
        return new StateMachine(numberOfStatesDFA, dfa, isTerminalDFA, this.startState, this.alphabet);
    }

    private ArrayList<ArrayList<Boolean>> buildTable(ArrayList<ListMultimap<String, Integer>> inverseSigma) {
        ArrayList<ArrayList<Boolean>> marked = new ArrayList<>();
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        for (int i = 0; i < this.n; i++) {
            marked.add(new ArrayList<>());
            for (int j = 0 ; j < this.n; j++) {
                marked.get(i).add(false);
            }
        }

        for (int i = 0; i < this.n; i++) {
            for (int j = 0; j < this.n; j++) {
                if (!marked.get(i).get(j) && i != j && !isTerminal.get(i).equals(isTerminal.get(j))) {
                    marked.get(i).set(j, true);
                    marked.get(j).set(i, true);
                    queue.add(new Pair<>(i, j));
                }
            }
        }
        while (!queue.isEmpty()) {
            Pair<Integer, Integer> direction = queue.poll();
            for (String word : this.alphabet) {
                for (Integer r: inverseSigma.get(direction.getKey()).get(word)) {
                    for (Integer s: inverseSigma.get(direction.getValue()).get(word)) {
                        if (!marked.get(r).get(s)) {
                            marked.get(r).set(s, true);
                            marked.get(s).set(r, true);
                            queue.add(new Pair<>(r, s));
                        }
                    }
                }
            }
        }

        return marked;
    }

    private ArrayList<ListMultimap<String, Integer>> buildInverseSigma() {
        ArrayList<ListMultimap<String, Integer>> inverseSigma = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            inverseSigma.add(ArrayListMultimap.create());
        }
        int countVertex = 0;
        for (ListMultimap<String, Integer> transitionByState : this.sigma) {
            for (String stringOfWords : transitionByState.keySet()) {
                List<Integer> words = transitionByState.get(stringOfWords);
                for (Integer state: words) {
                    inverseSigma.get(state).put(stringOfWords, countVertex);
                }
            }
            ++countVertex;
        }
        return inverseSigma;
    }

    private void buildReachableByDFS(int v, ArrayList<Boolean> reachable) {
        reachable.set(v, true);
        for (String stringOfWords : this.sigma.get(v).keySet()) {
            List<Integer> words = this.sigma.get(v).get(stringOfWords);
            for (Integer state: words) {
                if (!reachable.get(state)) {
                    this.buildReachableByDFS(state, reachable);
                }
            }
        }
    }

    private void log(String title, Object a) {
        System.out.println(title);
        System.out.print('\t');
        System.out.println(a);
    }
}
