package edu.kis.powp.jobs2d.command;

import java.util.ArrayList;
import java.util.Arrays;

import edu.kis.powp.jobs2d.command.visitor.ComplexCommandComparisonVisitor;

public class ComplexCommandComparisonTest {

    public static void main(String[] args) {

        ComplexCommandComparisonVisitor visitor = new ComplexCommandComparisonVisitor();

        // Identical simple commands are equal.
        SetPositionCommand a = new SetPositionCommand(1, 2);
        SetPositionCommand b = new SetPositionCommand(1, 2);
        if (!visitor.areEqual(a, b)) {
            throw new AssertionError("Identical SetPositionCommands should be equal");
        }

        // Different parameters make commands unequal.
        SetPositionCommand c = new SetPositionCommand(1, 3);
        if (visitor.areEqual(a, c)) {
            throw new AssertionError("SetPositionCommands with different coordinates should not be equal");
        }

        // Different command types are unequal even with the same parameters.
        OperateToCommand d = new OperateToCommand(1, 2);
        if (visitor.areEqual(a, d)) {
            throw new AssertionError("SetPositionCommand and OperateToCommand should not be equal");
        }

        // Structurally identical compound commands are equal, even with different names.
        CompoundCommand left = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))), "left");
        CompoundCommand right = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))), "right");
        if (!visitor.areEqual(left, right)) {
            throw new AssertionError("Structurally identical compound commands should be equal regardless of name");
        }

        // Order matters for compound commands.
        CompoundCommand reordered = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new OperateToCommand(2, 2), new SetPositionCommand(1, 1))), "left");
        if (visitor.areEqual(left, reordered)) {
            throw new AssertionError("Compound commands with reordered children should not be equal");
        }

        // Nested compound commands are compared recursively.
        CompoundCommand innerLeft = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(5, 5))), "inner");
        CompoundCommand outerLeft = new CompoundCommand(
                new ArrayList<>(Arrays.asList(innerLeft, new OperateToCommand(9, 9))), "outer");

        CompoundCommand innerRight = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(5, 5))), "different-inner-name");
        CompoundCommand outerRight = new CompoundCommand(
                new ArrayList<>(Arrays.asList(innerRight, new OperateToCommand(9, 9))), "different-outer-name");

        if (!visitor.areEqual(outerLeft, outerRight)) {
            throw new AssertionError("Nested compound commands with identical structure should be equal");
        }

        // Changing a nested leaf command makes the structures unequal.
        CompoundCommand innerDifferent = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(5, 6))), "inner");
        CompoundCommand outerDifferent = new CompoundCommand(
                new ArrayList<>(Arrays.asList(innerDifferent, new OperateToCommand(9, 9))), "outer");

        if (visitor.areEqual(outerLeft, outerDifferent)) {
            throw new AssertionError("Compound commands differing in a nested leaf should not be equal");
        }

        // Different number of children makes compound commands unequal.
        CompoundCommand shorter = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1))), "left");
        if (visitor.areEqual(left, shorter)) {
            throw new AssertionError("Compound commands with different child counts should not be equal");
        }

        // Comparing an ImmutableCompoundCommand to an equivalent CompoundCommand.
        ImmutableCompoundCommand immutable = new ImmutableCompoundCommand("immutable",
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))));
        if (!visitor.areEqual(left, immutable)) {
            throw new AssertionError("CompoundCommand and ImmutableCompoundCommand with same structure should be equal");
        }

        // null handling
        if (!visitor.areEqual(null, null)) {
            throw new AssertionError("Two null commands should be considered equal");
        }
        if (visitor.areEqual(a, null) || visitor.areEqual(null, a)) {
            throw new AssertionError("A non-null command should not equal null");
        }

        // Same instance compared to itself.
        if (!visitor.areEqual(a, a)) {
            throw new AssertionError("A command should be equal to itself");
        }

        System.out.println("TEST PASSED");
    }
}