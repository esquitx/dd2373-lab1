package automata;

import java.util.*;

/* TODO: Implement a grep-like tool. */

public class MyApplication {

    // Your own regex search function here:
    // Takes a regex and a text as input
    // Returns true if the regex matches any substring of the text; otherwise
    // returns false

    public static boolean mySearch(String regex, String text) throws Exception {

        // 3.1 - Read and parse the NFA (use provided code)
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        Set<Character> sigma = new HashSet<>();

        for (char c : alphabet.toCharArray())
            sigma.add(c);

        // 3.2 - Build eps-nfa from parsed expression
        // ->
        EpsNFA parserNFA = REParser.parse(regex).accept(new DiegoBuilder(sigma)); // -> ((((((ab)a)*)(c+))a)|(cd))
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
        dfa.printGV();

        // <-

        // 3.5 - Simulating the DFA
        // ->
        Set<Integer> endState = dfa.simulate(text);
        if (dfa.getAcceptingStates().contains(endState))
            System.out.println("Verified");
        else
            System.out.println("Not Verified!");

        // <-

        return true;
    }

    public static void main(String[] args) throws Exception {

        mySearch("(ab)+", "bcd");
        System.exit(0);
    }

}
