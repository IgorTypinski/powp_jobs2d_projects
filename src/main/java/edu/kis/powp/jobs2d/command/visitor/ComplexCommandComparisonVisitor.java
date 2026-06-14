package edu.kis.powp.jobs2d.command.visitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public class ComplexCommandComparisonVisitor implements ICommandVisitor {

    private static final class Token {
        private final String kind;
        private final int x;
        private final int y;

        private Token(String kind, int x, int y) {
            this.kind = kind;
            this.x = x;
            this.y = y;
        }

        private static Token of(String kind) {
            return new Token(kind, 0, 0);
        }

        private static Token of(String kind, int x, int y) {
            return new Token(kind, x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Token)) {
                return false;
            }
            Token other = (Token) o;
            return x == other.x && y == other.y && Objects.equals(kind, other.kind);
        }

        @Override
        public int hashCode() {
            return Objects.hash(kind, x, y);
        }

        @Override
        public String toString() {
            switch (kind) {
                case "SetPosition":
                case "OperateTo":
                    return kind + "(" + x + ", " + y + ")";
                default:
                    return kind;
            }
        }
    }

    private List<Token> tokens = new ArrayList<>();

    @Override
    public void visit(SetPositionCommand command) {
        tokens.add(Token.of("SetPosition", command.getPosX(), command.getPosY()));
    }

    @Override
    public void visit(OperateToCommand command) {
        tokens.add(Token.of("OperateTo", command.getPosX(), command.getPosY()));
    }

    @Override
    public void visit(ICompoundCommand command) {
        tokens.add(Token.of("CompoundStart"));
        for (DriverCommand child : (Iterable<DriverCommand>) command::iterator) {
            child.accept(this);
        }
        tokens.add(Token.of("CompoundEnd"));
    }

    public boolean areEqual(DriverCommand first, DriverCommand second) {
        if (first == second) {
            return true;
        }
        if (first == null || second == null) {
            return false;
        }

        List<Token> firstTokens = collectTokens(first);
        List<Token> secondTokens = collectTokens(second);
        return firstTokens.equals(secondTokens);
    }

    private List<Token> collectTokens(DriverCommand command) {
        reset();
        command.accept(this);
        return new ArrayList<>(tokens);
    }

    public void reset() {
        tokens.clear();
    }
}