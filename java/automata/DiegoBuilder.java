package automata;

import java.util.HashSet;
import java.util.Set;

import automata.resyntax.*;

public class DiegoBuilder implements RegExpVisitor {

    public Set<Character> sigma;

    public DiegoBuilder(Set<Character> sigma) {
        this.sigma = sigma;
    }

    public EpsNFA visit(Closure closure) {

        EpsNFA innerNFA = closure.r.accept(this);

        // 1. Add epsilon transition from accepting to initial
        for (int accepting : innerNFA.getAcceptingStates()) {
            innerNFA.addTransition(accepting, innerNFA.getInitialState(), EpsNFA.EPSILON);
        }

        // 2. Shift states by one
        EpsNFA closureNFA = innerNFA.shiftStates(1);
        closureNFA.setInitialState(0);
        closureNFA.addAcceptingState(closureNFA.getMaxState() + 1);

        // 2. Add epsilon transition from new initial to inner initial
        closureNFA.addTransition(closureNFA.getInitialState(), innerNFA.getInitialState() + 1, EpsNFA.EPSILON);

        // 3. Add epsilon transition from initial to new accepting
        closureNFA.addTransition(closureNFA.getInitialState(), closureNFA.getMaxState(), EpsNFA.EPSILON);

        // 4. Add epsilon transition from accepting in inner to accepting in EpsNFA
        for (int accepting : innerNFA.getAcceptingStates()) {
            closureNFA.addTransition(accepting + 1, closureNFA.getMaxState(), EpsNFA.EPSILON);
        }

        return closureNFA;
    }

    public EpsNFA visit(Concatenation concat) {

        EpsNFA left = concat.r1.accept(this);
        EpsNFA right = concat.r2.accept(this);

        // 1 - Shift states to right
        EpsNFA concatNFA = right.shiftStates(left.getMaxState() + 1);

        // 2. Set new initial state
        concatNFA.setInitialState(left.getInitialState());

        // 3. Add transitions from left
        for (int src : left.trans.keySet())
            for (int dst : left.trans.get(src).keySet())
                concatNFA.addTransitions(src, dst, left.trans.get(src).get(dst));

        // 4. Join accepting from left to initial from right with epsilon trans
        for (int accepting : left.getAcceptingStates()) {
            concatNFA.addTransition(accepting, right.getInitialState() + left.getMaxState() + 1, EpsNFA.EPSILON);
        }

        return concatNFA;

    }

    public EpsNFA visit(Dot dot) {

        // 0. Set up NFA
        EpsNFA dotNFA = new EpsNFA();
        dotNFA.addAcceptingState(dotNFA.getMaxState() + 1);

        // 1. Get alphabet (all possible characters)
        Set<Character> alphabet = dotNFA.getAlphabet();

        // 2. Add transitions for all characters
        dotNFA.addTransitions(dotNFA.getInitialState(), dotNFA.getFirstAcceptingState(), alphabet);

        return dotNFA;

    }

    public EpsNFA visit(Litteral litteral) {

        EpsNFA charNFA = new EpsNFA();

        charNFA.addAcceptingState(charNFA.getMaxState() + 1);
        charNFA.addTransition(charNFA.getInitialState(), charNFA.getFirstAcceptingState(), litteral.c);

        return charNFA;
    }

    public EpsNFA visit(OneOrMore oneOrMore) {

        EpsNFA innerNFA = oneOrMore.r.accept(this);

        // 0 . "copy" nfa
        EpsNFA newNFA = innerNFA.shiftStates(0);

        // 1. Connect accepting states to initial state with eps transitions
        for (int accepting : newNFA.getAcceptingStates()) {
            newNFA.addTransition(accepting, newNFA.getInitialState(), EpsNFA.EPSILON);
        }

        return newNFA;

    }

    public EpsNFA visit(ZeroOrOne zeroOrOne) {

        EpsNFA innerNFA = zeroOrOne.r.accept(this);

        // 0. - copy NFA
        EpsNFA newNFA = innerNFA.shiftStates(0);

        // 1. - Connect all initial state to all accepting states
        for (int accepting : newNFA.getAcceptingStates()) {
            newNFA.addTransition(newNFA.getInitialState(), accepting, EpsNFA.EPSILON);
        }
        return newNFA;
    }

    public EpsNFA visit(Union union) {

        EpsNFA left = union.r1.accept(this);
        EpsNFA right = union.r2.accept(this);

        // 1. "Parallelize" both automaton
        EpsNFA tempNFA = right.shiftStates(left.getMaxState() + 1);
        for (int src : left.trans.keySet())
            for (int dst : left.trans.get(src).keySet())
                tempNFA.addTransitions(src, dst, left.trans.get(src).get(dst));

        EpsNFA unionNFA = tempNFA.shiftStates(1);

        // 2. Set new initial and accepting states
        unionNFA.setInitialState(0);
        unionNFA.addAcceptingState(unionNFA.getMaxState() + 1);

        // 3. Add epsilon transitions from initial to initial
        unionNFA.addTransition(unionNFA.getInitialState(), left.getInitialState() + 1, EpsNFA.EPSILON);
        unionNFA.addTransition(unionNFA.getInitialState(), right.getInitialState() + left.getMaxState() + 2,
                EpsNFA.EPSILON);

        // 4. Add epsilon transitions from accepting to accepting
        for (int accepting : left.getAcceptingStates())
            unionNFA.addTransition(accepting + 1, unionNFA.getMaxState(), EpsNFA.EPSILON);
        for (int accepting : right.getAcceptingStates())
            unionNFA.addTransition(accepting + right.getMaxState() + 2, unionNFA.getMaxState(), EpsNFA.EPSILON);

        return unionNFA;

    }
}