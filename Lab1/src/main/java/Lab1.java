import com.google.common.collect.*;
import java.io.FileReader;
import java.util.*;

// Нехай відстань між двома словами – це кількість позицій, що відрізняються
// буквами. Знайти всі пари слів з найбільшою відстанню.

class Lab1 {
    final static String regex = "[^A-Za-z]";
    public static void main(String[] args) throws Exception {
        FileReader fr = new FileReader("test.txt");
//        FileReader fr = new FileReader("hard-test.csv");

        Scanner scan = new Scanner(fr);
        HashSet<String> wordsSet = new HashSet<>(), aSet = new HashSet<>();
        ArrayList<String> a = new ArrayList<>();
        try {
            while (scan.hasNextLine()) {
                String input = scan.nextLine();
                String[] words = input.split(regex);
                for(String word_: words){-
                    if(word_.length() == 0) continue;
                    String word = word_.length() > 30 ? word_.substring(0, 30) : word_;
                    if(wordsSet.add(word.toLowerCase())){
                        a.add(word.toLowerCase());
                    }
                }
            }
        } finally {
            scan.close();
        }
        HashMap<String, SortedSetMultimap<Integer, String>> ans = new HashMap<>();
        Collections.sort(a, (s1, s2) -> s1.length() -  s2.length());
        int size = a.size();
        int ansCounter = 0,  minCounter = comp(a.get(0), a.get(size - 1));
        for (int i = 0; i < size; i++) {
            ans.put(a.get(i), TreeMultimap.create());
            for (int j = 0; j < size; j++) {
                if (i == j ) continue;
                int curCount = comp(a.get(i), a.get(j));
                if (curCount >= minCounter) {
                    ansCounter = Math.max(ansCounter, curCount);
                    ans.get(a.get(i)).put(curCount, a.get(j));
                    minCounter = ansCounter;
                }
            }
        }
        int finalCount = ansCounter;
        ans.forEach((key, value) -> {
            if (value.containsKey(finalCount)) {
                for (String str: value.get(finalCount)) {
                    System.out.println(key + " " + str);
                }
            }
        });
    }


    public static int comp(String a, String b) {
        int ans = Math.abs(a.length() - b.length());
        for (int i = 0; i < Math.min(a.length(), b.length()); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                ++ans;
            }
        }
        return ans;
    }
}















