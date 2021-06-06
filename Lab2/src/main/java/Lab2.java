import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;


public class Lab2 {

    public static void main(String[] args) throws Exception {
//        String firstStateMachineFile = "test_1_1_lab_2.txt", secondStateMachineFile = "test_1_2_lab_2.txt"; // false
//        String firstStateMachineFile = "test_2_1_lab_2.txt", secondStateMachineFile = "test_2_2_lab_2.txt"; // true
        String firstStateMachineFile = "test_3_1_lab_2.txt", secondStateMachineFile = "test_3_2_lab_2.txt"; // true

        StateMachine firstMachine = new StateMachine(), secondMachine = new StateMachine();

        readStateMachineFromFile(firstMachine, firstStateMachineFile);
        readStateMachineFromFile(secondMachine, secondStateMachineFile);

//        StateMachine min = firstMachine.minimizeStateMachine();
//        min.print();;

        boolean isMachinesEquivalent = firstMachine.isMachineEquivalentTo(secondMachine);
        System.out.println("===========================================================================");
        System.out.println("State machines are equivalent :: " + isMachinesEquivalent);
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
