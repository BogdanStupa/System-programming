import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Lab2 {

    public static void main(String[] args) throws Exception {
        // Правильна відповідь: ab
        test_1();

        // Правильна відповідь: baba
        test_2();

        // Правильна відповідь: - (приклад з недетермінованим автоматом у файлі test_3_2)
        test_3();
    }

    public static void test_1() throws FileNotFoundException {
        StateMachine firstMachine = new StateMachine(), secondMachine = new StateMachine();
        readStateMachineFromFile(firstMachine, "test_1_1.txt");
        readStateMachineFromFile(secondMachine, "test_1_2.txt");

        System.out.print("Answer for 'test_1':\t");
        System.out.println(firstMachine.shortestStringWith(secondMachine));
    }

    public static void test_2() throws FileNotFoundException {
        StateMachine firstMachine = new StateMachine(), secondMachine = new StateMachine();
        readStateMachineFromFile(firstMachine, "test_2_1.txt");
        readStateMachineFromFile(secondMachine, "test_2_2.txt");

        System.out.print("Answer for 'test_2':\t");
        System.out.println(firstMachine.shortestStringWith(secondMachine));
    }
    public static void test_3() throws FileNotFoundException {
        StateMachine firstMachine = new StateMachine(), secondMachine = new StateMachine();
        readStateMachineFromFile(firstMachine, "test_3_1.txt");
        readStateMachineFromFile(secondMachine, "test_3_2.txt");

        secondMachine = secondMachine.toDFAbyNFA();

        System.out.print("Answer for 'test_3':\t");
        System.out.println(firstMachine.shortestStringWith(secondMachine));
    }

    public static void readStateMachineFromFile(StateMachine stateMachine, String fileName) throws FileNotFoundException {
        FileReader fr = new FileReader(fileName);
        Scanner scan = new Scanner(fr);
        try {
            int i = 0;
            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                String[] w = input.split(" ");
                switch (i) {
                    case 0 -> stateMachine.setNumberOfStates(Integer.parseInt(w[0]));
                    case 1 -> stateMachine.setStartState(0);
                    case 2 -> stateMachine.setTerminalStates(w);
                    case 3 -> stateMachine.setAlphabet(buildAlphabet(Integer.parseInt(w[0])));
                    default -> stateMachine.addTransition(Integer.parseInt(w[0]), w[1], Integer.parseInt(w[2]));
                }
                ++i;
            }
        } finally {
            scan.close();
        }
    }

    public static ArrayList<String> buildAlphabet(int n) {
        ArrayList<String> alphabet = new ArrayList<>();
        for (int i = 0; i < n; ++i) {
            alphabet.add(String.copyValueOf(new char[]{ (char)('a' + i)}));
        }
        return alphabet;
    }
}
