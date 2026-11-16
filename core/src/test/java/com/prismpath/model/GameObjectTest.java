package com.prismpath.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

class GameObjectTest {

    private static final float DELTA = 1e-4f;

    /** Minimale Unterklasse nur für Tests. */
    static final class Box extends GameObject {
        Box(float x, float y, float angle, boolean movable, boolean rotatable) {
            super(x, y, angle, 10, movable, rotatable);
        }
    }

    @Test
    void richtungFolgtDemWinkel() {
        Vector2 d = new Box(0, 0, 90, false, false).direction();
        assertEquals(0, d.x, DELTA);
        assertEquals(1, d.y, DELTA);
    }

    @Test
    void winkelWirdAufNullBisDreihundertsechzigNormalisiert() {
        assertEquals(350, new Box(0, 0, -10, false, false).angle, DELTA);
        assertEquals(10, new Box(0, 0, 370, false, false).angle, DELTA);
    }

    @Test
    void drehbaresObjektDrehtSich() {
        Box box = new Box(0, 0, 350, true, true);
        assertTrue(box.rotate(20));
        assertEquals(10, box.angle, DELTA);
    }

    @Test
    void festesObjektIgnoriertDrehung() {
        Box box = new Box(0, 0, 45, true, false);
        assertFalse(box.rotate(20));
        assertEquals(45, box.angle, DELTA);
    }

    @Test
    void objektBlockiertFiguren() {
        Box box = new Box(0, 0, 0, false, false);
        Vector2 player = new Vector2(15, 0);
        assertTrue(box.pushOut(player, 10));
        assertEquals(20, player.x, DELTA);
    }

    @Test
    void wandBlockiertFiguren() {
        Wall wall = new Wall(0, 0, 100, 20);
        Vector2 player = new Vector2(50, 25);
        assertTrue(wall.pushOut(player, 10));
        assertEquals(30, player.y, DELTA);
    }
}
