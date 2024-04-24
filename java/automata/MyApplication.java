package automata;

import java.util.*;

/* TODO: Implement a grep-like tool. */

public class MyApplication {

    // Your own regex search function here:
    // Takes a regex and a text as input
    // Returns true if the regex matches any substring of the text; otherwise
    // returns false

    public static boolean mySearch(String regex, String text) {

        return true;
    }

    public static void main(String[] args) throws Exception {

        // 3.1 - Read and parse the NFA (use provided code)

        // 3.2 - Build eps-nfa from parsed expression
        // ->
        EpsNFA parserNFA = REParser.parse("a*b").accept(new PrettyBuilder()); // -> ((((((ab)a)*)(c+))a)|(cd))
        parserNFA.printGV();
        // <-

        // 3.3 - Converting to equivalent DFA
        // ->
        DFA dfa = parserNFA.convertToDFA();
        dfa.printGV();
        // <-

        // 3.4 - Minimizing the DFA
        // ->
        dfa.minimize();
        dfa.printGV();

        // <-

        // 3.5 - Simulating the DFA
        // ->
        Set<Integer> endState = dfa.simulate("abc");
        if (dfa.getAcceptingStates().contains(endState))
            System.out.println("Verified");
        else
            System.out.println("Not Verified!");

        // <-
        System.exit(0);
    }

}
