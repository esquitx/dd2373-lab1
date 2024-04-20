package automata;

import java.util.*;
import javafx.util.Pair;

public class DFA extends Automaton<Set<Integer>, Character> {

    public DFA() {
    }

    public void minimize() {

        // We use Myhill-Nerode Theorem

        // Step 1: Create pairs (a, b) of all states in DFA

        // First create table
        Map<List<Set<Integer>>, Boolean> statePairs = new HashMap<>();

        for (Set<Integer> a : this.getStates()) {
            for (Set<Integer> b : this.getStates()) {
                if (!a.equals(b)) {
                    List<Set<Integer>> pair = new ArrayList<>();
                    pair.add(a);
                    pair.add(b);

                    if (!statePairs.containsKey(pair)) {
                        statePairs.put(pair, false);
                    }

                }
            }
        }

        // Step 2 : Mark pairs were a is final and b is non final

        for (List<Set<Integer>> pair : statePairs.keySet()) {
            Set<Integer> a = pair.get(0);
            Set<Integer> b = pair.get(1);
            if ((this.getAcceptingStates().contains(a) && !this.getAcceptingStates().contains(b))
                    || (this.getAcceptingStates().contains(a) && this.getAcceptingStates().contains(b))) {
                // mark
                statePairs.put(pair, true);
            }
        }

        // For an input symnbol x

        // Step 3 : If there is an unamrked pair (a, b) such that
        // d(a, x) and d(b, x) is marked, then mark (a, b)
        boolean marked;
        do {
            marked = false;
            for (List<Set<Integer>> pair : statePairs.keySet()) {
                if (!statePairs.get(pair)) { // Check unmarked pairs
                    Set<Integer> a = pair.get(0);
                    Set<Integer> b = pair.get(1);
                    for (Character x : this.getAlphabet()) {
                        if (!statePairs.get(pair)) {
                            Set<Set<Integer>> nextA = this.getSuccessors(a, x);
                            Set<Set<Integer>> nextB = this.getSuccessors(b, x);
                            for (Set<Integer> succA : nextA) {
                                for (Set<Integer> succB : nextB) {
                                    List<Set<Integer>> succPair = new ArrayList<>();
                                    succPair.add(succA);
                                    succPair.add(succB);
                                    if (statePairs.get(succPair)) {
                                        statePairs.put(pair, true);
                                        marked = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } while (marked);

        // Step 4: Combine all unmarked pairs and make them a single state
        List<Set<Integer>> finalStates = new ArrayList<>();
        for (List<Set<Integer>> pair : statePairs.keySet()) {
            if (!statePairs.get(pair)) {
                Set<Integer> newState = new HashSet<>();
                newState.addAll(pair.get(0));
                newState.addAll(pair.get(1));
                finalStates.add(newState);
            }
        }
    }
}
