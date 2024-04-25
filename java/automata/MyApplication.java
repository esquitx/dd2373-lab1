package automata;

import java.util.*;
import java.util.Scanner;

/* TODO: Implement a grep-like tool. */

public class MyApplication {

    // Your own regex search function here:
    // Takes a regex and a text as input
    // Returns true if the regex matches any substring of the text; otherwise
    // returns false

    public static boolean mySearch(String alphabet, String regex, String text) throws Exception {

        // 3.1 - Read and parse the NFA (use provided code)
        Set<Character> sigma = new HashSet<>();

        for (char c : alphabet.toCharArray())
            sigma.add(c);

        // 3.2 - Build eps-nfa from parsed expression
        // ->
        EpsNFA parserNFA = REParser.parse(regex).accept(new DiegoBuilder(sigma));
        // parserNFA.printGV();
        // <-

        // 3.3 - Converting to equivalent DFA
        // ->
        DFA dfa = parserNFA.convertToDFA();
        // dfa.printGV();
        // <-

        // 3.4 - Minimizing the DFA
        // ->
        dfa.minimize();
        // dfa.printGV();

        // <-

        // 3.5 - Simulating the DFA
        // ->
        Set<Integer> endState = dfa.simulate(text);

        return dfa.getAcceptingStates().contains(endState);
    }

    public static void main(String[] args) throws Exception {

        String alphabet;
        String regex;
        String text = "";
        Scanner s = new Scanner(System.in);

        // Read data
        alphabet = s.nextLine();
        regex = ".*" + s.nextLine();

        while (s.hasNextLine())
            text = s.nextLine();
        if (mySearch(alphabet, regex, text))
            System.out.println(text);

        System.exit(0);
    }

}
