package edu.kis.powp.jobs2d.command;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import edu.kis.powp.jobs2d.command.visitor.ComplexCommandComparisonVisitor;

class CommandComparisonTest {

    private final ComplexCommandComparisonVisitor comparator = new ComplexCommandComparisonVisitor();

    @Test
    void identicalSimpleCommandsAreEqual() {
        SetPositionCommand a = new SetPositionCommand(1, 2);
        SetPositionCommand b = new SetPositionCommand(1, 2);

        assertTrue(comparator.areEqual(a, b));
    }

    @Test
    void simpleCommandsWithDifferentCoordinatesAreNotEqual() {
        SetPositionCommand a = new SetPositionCommand(1, 2);
        SetPositionCommand b = new SetPositionCommand(1, 3);

        assertFalse(comparator.areEqual(a, b));
    }

    @Test
    void differentLeafCommandTypesAreNotEqual() {
        SetPositionCommand a = new SetPositionCommand(1, 2);
        OperateToCommand b = new OperateToCommand(1, 2);

        assertFalse(comparator.areEqual(a, b));
    }

    @Test
    void structurallyIdenticalCompoundsWithDifferentNamesAreEqual() {
        CompoundCommand left = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))), "left");
        CompoundCommand right = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))), "right");

        assertTrue(comparator.areEqual(left, right));
    }

    @Test
    void compoundsWithReorderedChildrenAreNotEqual() {
        CompoundCommand original = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))), "left");
        CompoundCommand reordered = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new OperateToCommand(2, 2), new SetPositionCommand(1, 1))), "left");

        assertFalse(comparator.areEqual(original, reordered));
    }

    @Test
    void nestedCompoundsWithIdenticalStructureAreEqual() {
        CompoundCommand innerLeft = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(5, 5))), "inner");
        CompoundCommand outerLeft = new CompoundCommand(
                new ArrayList<>(Arrays.asList(innerLeft, new OperateToCommand(9, 9))), "outer");

        CompoundCommand innerRight = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(5, 5))), "different-inner-name");
        CompoundCommand outerRight = new CompoundCommand(
                new ArrayList<>(Arrays.asList(innerRight, new OperateToCommand(9, 9))), "different-outer-name");

        assertTrue(comparator.areEqual(outerLeft, outerRight));
    }

    @Test
    void nestedCompoundsDifferingInALeafAreNotEqual() {
        CompoundCommand innerLeft = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(5, 5))), "inner");
        CompoundCommand outerLeft = new CompoundCommand(
                new ArrayList<>(Arrays.asList(innerLeft, new OperateToCommand(9, 9))), "outer");

        CompoundCommand innerDifferent = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(5, 6))), "inner");
        CompoundCommand outerDifferent = new CompoundCommand(
                new ArrayList<>(Arrays.asList(innerDifferent, new OperateToCommand(9, 9))), "outer");

        assertFalse(comparator.areEqual(outerLeft, outerDifferent));
    }

    @Test
    void compoundsWithDifferentChildCountsAreNotEqual() {
        CompoundCommand full = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))), "left");
        CompoundCommand shorter = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1))), "left");

        assertFalse(comparator.areEqual(full, shorter));
    }

    @Test
    void compoundCommandAndImmutableCompoundCommandWithSameStructureAreEqual() {
        CompoundCommand left = new CompoundCommand(
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))), "left");
        ImmutableCompoundCommand immutable = new ImmutableCompoundCommand("immutable",
                new ArrayList<>(Arrays.asList(new SetPositionCommand(1, 1), new OperateToCommand(2, 2))));

        assertTrue(comparator.areEqual(left, immutable));
    }

    @Test
    void commandIsEqualToItself() {
        SetPositionCommand a = new SetPositionCommand(1, 2);

        assertTrue(comparator.areEqual(a, a));
    }
}