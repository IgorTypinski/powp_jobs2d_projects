package edu.kis.powp.jobs2d.command.visitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.kis.powp.jobs2d.command.DriverCommand;
import edu.kis.powp.jobs2d.command.ICompoundCommand;
import edu.kis.powp.jobs2d.command.OperateToCommand;
import edu.kis.powp.jobs2d.command.SetPositionCommand;

public class ComplexCommandComparisonVisitor {

    public boolean areEqual(DriverCommand first, DriverCommand second) {
        if (first == second) {
            return true;
        }

        return signatureOf(first).equals(signatureOf(second));
    }

    public boolean areEqual(ICompoundCommand first, ICompoundCommand second) {
        return areEqual((DriverCommand) first, (DriverCommand) second);
    }

    private List<CommandToken> signatureOf(DriverCommand command) {
        SignatureVisitor signatureVisitor = new SignatureVisitor();
        command.accept(signatureVisitor);
        return signatureVisitor.getSignature();
    }

    private abstract static class CommandToken {

        static final class CompoundStart extends CommandToken {
            static final CompoundStart INSTANCE = new CompoundStart();

            private CompoundStart() {
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof CompoundStart;
            }

            @Override
            public int hashCode() {
                return CompoundStart.class.hashCode();
            }

            @Override
            public String toString() {
                return "CompoundStart";
            }
        }

        static final class CompoundEnd extends CommandToken {
            static final CompoundEnd INSTANCE = new CompoundEnd();

            private CompoundEnd() {
            }

            @Override
            public boolean equals(Object o) {
                return o instanceof CompoundEnd;
            }

            @Override
            public int hashCode() {
                return CompoundEnd.class.hashCode();
            }

            @Override
            public String toString() {
                return "CompoundEnd";
            }
        }

        static final class PositionalLeaf extends CommandToken {
            private final String kind;
            private final int x;
            private final int y;

            PositionalLeaf(String kind, int x, int y) {
                this.kind = Objects.requireNonNull(kind, "kind must not be null");
                this.x = x;
                this.y = y;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof PositionalLeaf)) {
                    return false;
                }
                PositionalLeaf other = (PositionalLeaf) o;
                return x == other.x && y == other.y && kind.equals(other.kind);
            }

            @Override
            public int hashCode() {
                return Objects.hash(kind, x, y);
            }

            @Override
            public String toString() {
                return kind + "(" + x + ", " + y + ")";
            }
        }
    }

    private static final class SignatureVisitor implements ICommandVisitor {

        private final List<CommandToken> tokens = new ArrayList<>();

        @Override
        public void visit(SetPositionCommand command) {
            tokens.add(new CommandToken.PositionalLeaf("SetPosition", command.getPosX(), command.getPosY()));
        }

        @Override
        public void visit(OperateToCommand command) {
            tokens.add(new CommandToken.PositionalLeaf("OperateTo", command.getPosX(), command.getPosY()));
        }

        @Override
        public void visit(ICompoundCommand command) {
            tokens.add(CommandToken.CompoundStart.INSTANCE);
            for (DriverCommand child : (Iterable<DriverCommand>) command::iterator) {
                child.accept(this);
            }
            tokens.add(CommandToken.CompoundEnd.INSTANCE);
        }

        List<CommandToken> getSignature() {
            return Collections.unmodifiableList(new ArrayList<>(tokens));
        }
    }
}