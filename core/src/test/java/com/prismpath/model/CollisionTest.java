package com.prismpath.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

class CollisionTest {

    private static final float DELTA = 1e-4f;
    private final Rectangle wall = new Rectangle(0, 0, 100, 20);

    @Test
    void kreisNebenRechteckUeberlapptNicht() {
        assertFalse(Collision.overlaps(new Vector2(50, 40), 10, wall));
    }

    @Test
    void beruehrenZaehltNichtAlsUeberlappung() {
        assertFalse(Collision.overlaps(new Vector2(50, 30), 10, wall));
    }

    @Test
    void kreisWirdSenkrechtAusDerOberkanteGeschoben() {
        Vector2 c = new Vector2(50, 25);
        assertTrue(Collision.pushOut(c, 10, wall));
        assertEquals(50, c.x, DELTA);
        assertEquals(30, c.y, DELTA);
    }

    @Test
    void kreisWirdAnDerEckeDiagonalHerausgeschoben() {
        Vector2 c = new Vector2(105, 25);
        assertTrue(Collision.pushOut(c, 10, wall));
        assertEquals(10, c.dst(100, 20), DELTA);
    }

    @Test
    void mittelpunktImRechteckGehtUeberDieNaechsteKante() {
        Vector2 c = new Vector2(95, 10);
        assertTrue(Collision.pushOut(c, 5, wall));
        assertEquals(105, c.x, DELTA);
        assertEquals(10, c.y, DELTA);
    }

    @Test
    void keinePushOhneUeberlappung() {
        Vector2 c = new Vector2(50, 50);
        assertFalse(Collision.pushOut(c, 10, wall));
        assertEquals(new Vector2(50, 50), c);
    }

    @Test
    void zweiKreiseWerdenAufBeruehrabstandGetrennt() {
        Vector2 a = new Vector2(15, 0);
        Vector2 b = new Vector2(0, 0);
        assertTrue(Collision.overlaps(a, 10, b, 10));
        assertTrue(Collision.pushOut(a, 10, b, 10));
        assertEquals(20, a.dst(b), DELTA);
        assertEquals(new Vector2(0, 0), b);
    }
}
