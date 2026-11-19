package com.prismpath.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prismpath.model.LevelSolvedTest.FakeGoal;
import org.junit.jupiter.api.Test;

class LevelDoorTest {

    private static final float DELTA = 1e-3f;
    private final Level level = new Level(800, 600);
    private final Wall door = level.setDoor(new Wall(0, 250, 20, 100));
    private final FakeGoal goal = level.addObject(new FakeGoal());
    private final Player p1 = level.addPlayer(new Player(1, 60, 280, 180));
    private final Player p2 = level.addPlayer(new Player(2, 60, 320, 180));

    @Test
    void geschlosseneTuerStopptFigur() {
        p1.move(-1, 0, 1f, level);
        assertEquals(20 + Player.RADIUS, p1.pos.x, DELTA);
        assertFalse(door.rect.contains(p1.pos));
    }

    @Test
    void tuerOeffnetSichWennAlleZieleLeuchten() {
        assertFalse(level.isDoorOpen());
        goal.lit = true;
        assertTrue(level.isDoorOpen());
        p1.move(-1, 0, 1f, level);
        assertTrue(door.rect.contains(p1.pos));
        goal.lit = false;
        assertFalse(level.isDoorOpen(), "erlischt ein Ziel, ist die Tür wieder zu");
    }

    @Test
    void eineFigurInDerTuerBeendetDasLevelNicht() {
        goal.lit = true;
        p1.move(-1, 0, 1f, level);
        assertFalse(level.isFinished());
        p2.move(-1, 0, 1f, level);
        assertTrue(level.isFinished());
    }

    @Test
    void levelOhneTuerEndetNie() {
        Level noDoor = new Level(800, 600);
        noDoor.addObject(new FakeGoal()).lit = true;
        noDoor.addPlayer(new Player(1, 10, 10, 0));
        assertFalse(noDoor.isFinished());
    }
}
