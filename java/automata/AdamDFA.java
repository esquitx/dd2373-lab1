package automata;

import java.util.*;

public class DFA extends Automaton<Set<Integer>, Character>{
    Set<Character> sigma;
    public DFA(EpsNFA epsNFA, Set<Character> sigma) {
        this.setInitialState(new HashSet<>());
        this.sigma = sigma;
        dfaToNfa(epsNFA);
    }

    public void dfaToNfa(EpsNFA epsNFA) {

        // 1. set initial state of DFA
        this.setInitialState(epsNFA.epsClosure(epsNFA.getInitialState()));

        // 2. build transition function for DFA
        List<Set<Integer>> toVisit = new ArrayList<Set<Integer>>();
        toVisit.add(this.getInitialState());
        Set<Set<Integer>> visited = new HashSet<>();

        while (!toVisit.isEmpty()) {
            System.out.println(getStates());
            // pick unvisited dfaSrc in DFA
            Set<Integer> dfaSrc = toVisit.remove(0);
            visited.add(dfaSrc);

            // perform 1 step of BFS on dfaSrc
            for (Character x : sigma) {

                // create a set of NFA states reachable by x
                Set<Integer> dfaDstOnX = new HashSet<>();
                for (Integer nfaSrc : dfaSrc)
                    dfaDstOnX.addAll(epsNFA.getSuccessors(nfaSrc,x));

                // the next DFA state is their epsilon closure
                // find the closure and add transition
                dfaDstOnX = epsNFA.epsClosure(dfaDstOnX);
                this.addTransition(dfaSrc, dfaDstOnX, x);

                // add the e-closed set (dfa destination) to the queue
                if (!visited.contains(dfaDstOnX))
                    toVisit.add(dfaDstOnX);
            }
        }

        // 3. set accepting states for DFA
        for (Set<Integer> state : this.getStates()) {
            for (Integer nfaAccepting : epsNFA.epsClosure(epsNFA.getFirstAcceptingState())) { // probably unnecessary since the closure should be just the state
                if (state.contains(nfaAccepting))
                    this.addAcceptingState(state);
            }
        }
    }

    public void minimize() {
        String[][] table = new String[getStates().size()][getStates().size()];
        Set<Integer>[] states = getStates().toArray(new Set[0]);
        HashMap<Set<Integer>, Integer> stateToIndex = new HashMap<>();
        for (int i = 0; i < states.length; i++) {
            stateToIndex.put(states[i], i);
        }

        System.out.println("States: " + Arrays.toString(states));

        // Step 2 mark states {p,q} with epsilon if p in F and q not in F or vice versa
        for (int i = 0; i < states.length; i++) {
            for (int j = i+1; j < states.length; j++) {
                Set<Integer> p = states[i];
                Set<Integer> q = states[j];
                if (accepting.contains(p) ^ accepting.contains(q))
                    table[i][j] = EpsNFA.EPSILON.toString();
            }
        }

        // Step 3 for unmarked {p,q} & any a in sigma,
        // such that {delta(p,a), delta(q,a)} is marked by x,
        // mark {p,q} with ax
        for (int i = 0; i < states.length; i++) {
            for (int j = i+1; j < states.length; j++) {
                if (table[i][j] == null) {
                    Set<Integer> p = states[i];
                    Set<Integer> q = states[j];
                    for (Character c : sigma) {
                        // get delta(p,c) and delta(q,c) -> resulting in 2 new states
                        Set<Integer> deltaPC = getSuccessors(p, c).iterator().next();
                        Set<Integer> deltaQC = getSuccessors(q, c).iterator().next();

                        // get indices of delta(p,c) and delta(q,c) in the table
                        int ii = Math.min(stateToIndex.get(deltaPC), stateToIndex.get(deltaQC));
                        int jj = Math.max(stateToIndex.get(deltaPC), stateToIndex.get(deltaQC));

                        // if table[ii][jj] is not null, update table[i][j]
                        if (table[ii][jj] != null) {
                            if (table[ii][jj].equals(EpsNFA.EPSILON.toString()))
                                table[i][j] = c.toString();
                            else
                                table[i][j] = c.toString() + table[ii][jj];
                        }
                    }
                }
            }
        }

        // 4. create equivalence classes
        Set<Set<Set<Integer>>> equivalenceClasses = new HashSet<>();

        for (int i = 0; i < states.length; i++) {
            for (int j = i+1; j < states.length; j++) {
                if (table[i][j] == null) {
                    Set<Integer> p = states[i];
                    Set<Integer> q = states[j];
                    boolean added = false;
                    // if a pair was already found (3+ in the equivalence class)
                    for (Set<Set<Integer>> equivalenceClass : equivalenceClasses) {
                        if (equivalenceClass.contains(p) || equivalenceClass.contains(q)) {
                            equivalenceClass.add(p);
                            equivalenceClass.add(q);
                            added = true;
                        }
                    }
                    // if neither of the states have been added before (first 2)
                    if (!added)
                        equivalenceClasses.add(Set.of(p,q));
                }
            }
        }

        // 5. in each equivalence class, merge the equivalent states into the first one to minimize DFA
        for (Set<Set<Integer>> equivalentStates : equivalenceClasses) {
            Set<Integer> mergeInto = equivalentStates.iterator().next();
            for (Set<Integer> mergeFrom : equivalentStates)
                this.mergeStates(mergeInto, mergeFrom);
        }

//        // print table
//        for (int i = 0; i < states.length; i++) {
//            for (int j = i+1; j < states.length; j++) {
//                System.out.print("(" + i + "" + j + "" + table[i][j] + ") ");
//            }
//            System.out.println();
//        }
//        // print equivalence classes
//        System.out.println(equivalenceClasses);
    }
}
