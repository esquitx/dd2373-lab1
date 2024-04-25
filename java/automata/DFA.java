package automata;

import java.util.*;

public class DFA extends Automaton<Set<Integer>, Character> {

    public DFA() {
    }

    public void minimize() {

        // We use Myhill-Nerode Theorem

        // Step 1: Create pairs (a, b) of all states in DFA
        Set<List<Set<Integer>>> statePairs = new HashSet<>();
        for (Set<Integer> a : getStates()) {
            for (Set<Integer> b : getStates()) {
                if (!a.equals(b)) {
                    List<Set<Integer>> pair = Arrays.asList(a, b);
                    statePairs.add(pair);
                }
            }
        }

        // Step 2: Mark pairs where one state is final and the other is non-final
        Map<List<Set<Integer>>, Boolean> markedPairs = new HashMap<>();
        for (List<Set<Integer>> pair : statePairs) {
            markedPairs.put(pair, false);
        }
        for (List<Set<Integer>> pair : statePairs) {
            Set<Integer> a = pair.get(0);
            Set<Integer> b = pair.get(1);
            boolean marked = (getAcceptingStates().contains(a) && !getAcceptingStates().contains(b)) ||
                    (getAcceptingStates().contains(b) && !getAcceptingStates().contains(a));
            markedPairs.put(pair, marked);
        }

        // Step 3: Mark pairs based on transitions
        boolean marked;
        do {
            // System.out.print("marked pairs: ");
            // System.out.println(markedPairs);
            marked = false;
            for (List<Set<Integer>> pair : statePairs) {
                if (!markedPairs.get(pair)) {
                    Set<Integer> a = pair.get(0);
                    Set<Integer> b = pair.get(1);
                    for (char symbol : getAlphabet()) {
                        Set<Set<Integer>> nextA = getSuccessors(a, symbol);
                        Set<Set<Integer>> nextB = getSuccessors(b, symbol);
                        if (!nextA.isEmpty() && !nextB.isEmpty()) {
                            List<Set<Integer>> nextPair = Arrays.asList(nextA.iterator().next(),
                                    nextB.iterator().next());
                            // add sanity check (next pair in keys we're interested in)
                            // why? before, it could happen (a, a) was a next pair, but we don't care about
                            // repeatred pairs.
                            if (markedPairs.keySet().contains(nextPair))
                                if (markedPairs.get(nextPair)) {
                                    markedPairs.put(pair, true);
                                    marked = true;
                                    break;
                                }
                        }
                    }
                }
            }
        } while (marked);

        // Step 4: Combine all unmarked pairs and make them a single state
        for (List<Set<Integer>> pair : statePairs) {
            if (!markedPairs.get(pair)) {
                Set<Integer> newState = new HashSet<>();
                newState.addAll(pair.get(0));
                newState.addAll(pair.get(1));
                mergeStates(newState, pair.get(0));
                mergeStates(newState, pair.get(1));
            }
        }
    }

    // Simulates the DFA, returns the state in which we stop.
    public Set<Integer> simulate(String input) {

        Set<Integer> currentState = getInitialState();

        for (char c : input.toCharArray()) {

            Set<Set<Integer>> successors = getSuccessors(currentState, c);

            if (successors != null && !successors.isEmpty()) {
                // there should only be one, since DFA is minimized
                currentState = successors.iterator().next();
            }
        }

        return currentState;
    }
}
