package automata;

import java.util.*;

public class EpsNFA extends Automaton<Integer, Character> {

    public final static Character EPSILON = '\u03B5';
    public static Integer STATE_COUNTER = 0;

    public EpsNFA() {
        initial = 0;
        STATE_COUNTER = 0;
    }

    public Integer getMaxState() {
        return Collections.max(getStates());
    }

    public EpsNFA shiftStates(int delta) {
        EpsNFA newNfa = new EpsNFA();

        for (int src : trans.keySet())
            for (int dst : trans.get(src).keySet())
                newNfa.addTransitions(src + delta, dst + delta, trans.get(src).get(dst));

        for (int acc : accepting)
            newNfa.addAcceptingState(acc + delta);

        newNfa.setInitialState(initial + delta);

        return newNfa;
    }

    public Set<Integer> epsClosure(Integer q) {
        List<Integer> toVisit = new ArrayList<Integer>();
        toVisit.add(q);

        Set<Integer> closure = new HashSet<Integer>();

        while (!toVisit.isEmpty()) {
            Integer p = toVisit.remove(0);
            closure.add(p);

            if (trans.containsKey(p))
                for (Integer dst : trans.get(p).keySet())
                    if (trans.get(p).get(dst).contains(EPSILON))
                        if (!closure.contains(dst))
                            toVisit.add(dst);
        }

        return closure;
    }

    public DFA convertToDFA() {

        // Function that transforms and instance of an EpsNFA and converts it into an
        // equivalent DFA

        DFA dfa = new DFA();

        // 1 - Set initial state to be eps closure of eps nfa initial state
        Set<Integer> initialState = epsClosure(getInitialState());
        dfa.initial = initialState;

        // Step 2: Initialize a queue to perform BFS on the DFA states
        Queue<Set<Integer>> unprocessedStates = new LinkedList<>();
        unprocessedStates.add(initialState);

        // Step 3: Initialize a set to keep track of visited DFA states
        Set<Set<Integer>> visitedStates = new HashSet<>();
        visitedStates.add(initialState);

        // Step 4: Perform BFS to construct DFA
        while (!unprocessedStates.isEmpty()) {
            Set<Integer> currentState = unprocessedStates.poll();

            // For each possible input symbol
            for (char symbol : getAllInputSymbols()) {
                // Step 4a: Compute the set of states reachable from the current state under the
                // symbol

                Set<Integer> nextState = new HashSet<>();

                for (int state : currentState) {
                    Set<Integer> transitions = getSuccessors(state, symbol);
                    if (transitions != null) {
                        nextState.addAll(transitions);
                    }
                }

                // Step 4b: Compute the epsilon closure of the computed next state
                Set<Integer> nextStateEpsilonClosure = new HashSet<>();
                for (int state : nextState) {
                    nextStateEpsilonClosure.addAll(epsClosure(state));
                }

                // Step 4c: Add the transition to the DFA
                if (!nextStateEpsilonClosure.isEmpty()) {
                    dfa.addTransition(currentState, nextStateEpsilonClosure, symbol);
                }

                // Step 4d: Add the nextStateEpsilonClosure to unprocessedStates if not visited
                if (!visitedStates.contains(nextStateEpsilonClosure)) {
                    unprocessedStates.add(nextStateEpsilonClosure);
                    visitedStates.add(nextStateEpsilonClosure);
                }
            }
        }

        // Step 5: Mark the states of DFA which contain final state of NFA as final
        // states of DFA
        for (Set<Integer> state : visitedStates) {
            for (int s : state) {
                if (getAcceptingStates().contains(s)) {
                    dfa.addAcceptingState(state);
                    break;
                }
            }
        }

        return dfa;
    }

    private Set<Character> getAllInputSymbols() {
        Set<Character> symbols = new HashSet<>();
        for (Map<Integer, Set<Character>> transitions : trans.values()) {
            for (Set<Character> chars : transitions.values()) {
                for (char c : chars) {
                    if (c != EPSILON) {
                        symbols.add(c);
                    }
                }
            }
        }

        return symbols;
    }

}