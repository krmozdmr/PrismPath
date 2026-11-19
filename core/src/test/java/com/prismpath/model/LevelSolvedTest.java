package com.prismpath.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LevelSolvedTest {

    /** Ziel, dessen Zustand der Test direkt setzt. */
    static final class FakeGoal extends GameObject implements Goal {
        boolean lit;

        FakeGoal() {
            super(0, 0, 0, 10, false, false);
        }

        @Override
        public boolean isLit() {
            return lit;
        }
    }

    private final Level level = new Level(800, 600);

    @Test
    void levelOhneZieleIstNichtGeloest() {
        assertFalse(level.isSolved());
    }

    @Test
    void einDunklesZielReicht() {
        FakeGoal a = level.addObject(new FakeGoal());
        FakeGoal b = level.addObject(new FakeGoal());
        a.lit = true;
        assertFalse(level.isSolved());
        b.lit = true;
        assertTrue(level.isSolved());
    }
}
