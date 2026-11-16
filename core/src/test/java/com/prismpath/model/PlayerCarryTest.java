package com.prismpath.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerCarryTest {

    private static final float DELTA = 1e-3f;
    private final Level level = new Level(800, 600);
    private final Player p1 = level.addPlayer(new Player(1, 100, 300, 0));

    private GameObject box(float x, float y, boolean movable, boolean rotatable) {
        return level.addObject(new GameObjectTest.Box(x, y, 0, movable, rotatable));
    }

    @Test
    void nimmtObjektVorDerFigurAuf() {
        GameObject box = box(140, 300, true, true);
        assertTrue(p1.pickUp(level));
        assertSame(box, p1.held);
    }

    @Test
    void objektHinterDerFigurWirdNichtAufgenommen() {
        box(60, 300, true, true);
        assertFalse(p1.pickUp(level));
    }

    @Test
    void objektAusserReichweiteWirdNichtAufgenommen() {
        box(300, 300, true, true);
        assertFalse(p1.pickUp(level));
    }

    @Test
    void festesObjektWirdNichtAufgenommen() {
        box(140, 300, false, true);
        assertFalse(p1.pickUp(level));
    }

    @Test
    void naechstesObjektGewinnt() {
        box(150, 300, true, true); // auch in Reichweite, aber weiter weg
        GameObject near = box(135, 330, true, true);
        assertTrue(p1.pickUp(level));
        assertSame(near, p1.held);
    }

    @Test
    void getragenesObjektFolgtDerFigur() {
        GameObject box = box(140, 300, true, true);
        p1.pickUp(level);
        p1.move(0, 1, 0.5f, level);
        assertEquals(p1.pos.x, box.pos.x, DELTA);
        assertTrue(box.pos.y > p1.pos.y, "Objekt liegt in Blickrichtung (oben)");
    }

    @Test
    void zweiteFigurKannGetragenesObjektNichtNehmen() {
        box(140, 300, true, true);
        p1.pickUp(level);
        Player p2 = level.addPlayer(new Player(2, 140, 360, 270));
        assertFalse(p2.pickUp(level));
    }

    @Test
    void ablegenGibtObjektFrei() {
        GameObject box = box(140, 300, true, true);
        p1.pickUp(level);
        assertTrue(p1.drop(level));
        assertNull(p1.held);
        assertTrue(level.solidObjects().contains(box));
    }

    @Test
    void ablegenInDerWandIstNichtErlaubt() {
        box(140, 300, true, true);
        p1.pickUp(level);
        level.addWall(new Wall(130, 0, 20, 600));
        assertFalse(p1.drop(level));
        assertTrue(p1.isHolding());
    }

    @Test
    void getragenesObjektLaesstSichDrehen() {
        GameObject box = box(140, 300, true, true);
        p1.pickUp(level);
        assertTrue(p1.rotateHeld(30));
        assertEquals(30, box.angle, DELTA);
    }

    @Test
    void nichtDrehbaresObjektBleibtBeimDrehen() {
        GameObject box = box(140, 300, true, false);
        p1.pickUp(level);
        assertFalse(p1.rotateHeld(30));
        assertEquals(0, box.angle, DELTA);
    }
}
