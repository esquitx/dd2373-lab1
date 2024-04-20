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
        EpsNFA parserNFA = REParser.parse("(aba)*c+a|cd").accept(new PrettyBuilder()); // -> ((((((ab)a)*)(c+))a)|(cd))
        // <-

        // 3.3 - Converting to equivalent DFA
        // ->
        DFA dfa = parserNFA.convertToDFA();
        // <-

        // 3.4 - Minimizing the DFA
        // ->
        dfa.minimize();
        // <-

        // 3.5 - Simulating the DFA
        // ->

        // <-

        System.exit(0);

    }

}
