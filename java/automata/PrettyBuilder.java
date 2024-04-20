package automata;

import java.util.HashSet;
import java.util.Set;

import automata.resyntax.*;

public class PrettyBuilder implements RegExpVisitor {

    public EpsNFA visit(Closure closure) {

        EpsNFA prevNFA = closure.r.accept(this);

        EpsNFA newNFA = new EpsNFA();

        // 1 - eps transition first two states ->
        newNFA.addTransition(newNFA.getInitialState(), prevNFA.getInitialState(), EpsNFA.EPSILON);
        // 2 - eps transition first and last ->
        newNFA.addTransition(newNFA.getInitialState(), newNFA.getMaxState() + 1, EpsNFA.EPSILON);
        // 3 - eps transition from previous to new
        // TODO : figure out how to deal with multiple accepting
        // USE FOR LOOP??
        for (int accepting : prevNFA.getAcceptingStates()) {
            newNFA.addTransition(accepting, prevNFA.getInitialState(), EpsNFA.EPSILON);
            newNFA.addTransition(accepting, newNFA.getMaxState() + 1, EpsNFA.EPSILON);
        }
        // 4 - add new accepting state
        newNFA.addAcceptingState(newNFA.getMaxState() + 1);

        return newNFA;
    }

    public EpsNFA visit(Concatenation concat) {
        EpsNFA left = concat.r1.accept(this);
        EpsNFA right = concat.r2.accept(this);

        EpsNFA newNFA = new EpsNFA();

        // 1 - connect accepting state of left with initial of right
        for (int accepting : left.getAcceptingStates()) {
            newNFA.addTransition(accepting, right.getInitialState(), EpsNFA.EPSILON);

        }
        // 2 - merge both nfas
        newNFA.mergeStates(left.shiftStates(1).getInitialState(),
                right.shiftStates(left.getStates().size() + 1).getInitialState());

        return newNFA;
    }

    public EpsNFA visit(Dot dot) {

        EpsNFA newNFA = new EpsNFA();

        Set<Character> alphabet = new HashSet<>();

        for (char c = Character.MIN_VALUE; c < Character.MAX_VALUE; c++) {
            alphabet.add(c);
        }

        for (char c : alphabet) {
            newNFA.addTransition(newNFA.getInitialState(), newNFA.getMaxState(), c);
        }

        newNFA.addAcceptingState(newNFA.getMaxState() + 1);

        return newNFA;
    }

    public EpsNFA visit(Litteral litteral) {

        EpsNFA charNFA = new EpsNFA();

        charNFA.addTransition(charNFA.getInitialState(), charNFA.getMaxState(), litteral.c);

        return charNFA;
    }

    public EpsNFA visit(OneOrMore oneOrMore) {

        EpsNFA prevNFA = oneOrMore.r.accept(this);

        EpsNFA newNFA = new EpsNFA();

        // 1 - Connect all accepting to initial
        for (int accepting : prevNFA.getAcceptingStates()) {
            newNFA.addTransition(accepting, prevNFA.getInitialState(), EpsNFA.EPSILON);
        }

        // 2 - Merge
        newNFA.mergeStates(prevNFA.shiftStates(1).getInitialState(), prevNFA.getInitialState());

        return newNFA;

    }

    public EpsNFA visit(ZeroOrOne zeroOrOne) {
        EpsNFA prevNFA = zeroOrOne.r.accept(this);

        // 1 - Connect all accepting states to the initial state
        for (int accepting : prevNFA.getAcceptingStates()) {
            prevNFA.addTransition(accepting, prevNFA.getInitialState(), EpsNFA.EPSILON);
        }

        return prevNFA;
    }

    public EpsNFA visit(Union union) {

        EpsNFA left = union.r1.accept(this);
        EpsNFA right = union.r2.accept(this);

        EpsNFA newNFA = new EpsNFA();

        newNFA.mergeStates(left.shiftStates(1).getInitialState(),
                right.shiftStates(left.getStates().size() + 1).getInitialState());

        return newNFA;

    }
}