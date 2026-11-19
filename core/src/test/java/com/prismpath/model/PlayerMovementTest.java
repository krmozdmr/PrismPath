package com.prismpath.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerMovementTest {

    private static final float DELTA = 1e-3f;
    private final Level level = new Level(800, 600);

    @Test
    void laeuftFreiInAlleRichtungen() {
        Player p = level.addPlayer(new Player(1, 400, 300, 0));
        p.move(0, 1, 0.5f, level);
        assertEquals(400, p.pos.x, DELTA);
        assertEquals(300 + Player.SPEED * 0.5f, p.pos.y, DELTA);
        assertEquals(90, p.facing, DELTA);
    }

    @Test
    void schraegIstNichtSchneller() {
        Player p = level.addPlayer(new Player(1, 400, 300, 0));
        p.move(1, 1, 0.5f, level);
        assertEquals(Player.SPEED * 0.5f, p.pos.dst(400, 300), DELTA);
        assertEquals(45, p.facing, DELTA);
    }

    @Test
    void ohneEingabeBleibtBlickrichtung() {
        Player p = level.addPlayer(new Player(1, 400, 300, 180));
        p.move(0, 0, 1f, level);
        assertEquals(180, p.facing, DELTA);
        assertEquals(400, p.pos.x, DELTA);
    }

    @Test
    void gleitetAnDerWandEntlang() {
        level.addWall(new Wall(0, 400, 800, 20));
        Player p = level.addPlayer(new Player(1, 100, 400 - Player.RADIUS, 0));
        p.move(1, 1, 0.5f, level);
        assertTrue(p.pos.x > 150, "Figur soll seitlich weiterlaufen");
        assertEquals(400 - Player.RADIUS, p.pos.y, DELTA);
    }

    @Test
    void laeuftNichtDurchDuenneWaendeBeiGrossemZeitschritt() {
        level.addWall(new Wall(500, 0, 4, 600));
        Player p = level.addPlayer(new Player(1, 400, 300, 0));
        p.move(1, 0, 2f, level);
        assertEquals(500 - Player.RADIUS, p.pos.x, DELTA);
    }

    @Test
    void bleibtImLevel() {
        Player p = level.addPlayer(new Player(1, 50, 50, 0));
        p.move(-1, -1, 5f, level);
        assertEquals(Player.RADIUS, p.pos.x, DELTA);
        assertEquals(Player.RADIUS, p.pos.y, DELTA);
    }

    @Test
    void figurenBlockierenSichGegenseitig() {
        Player p1 = level.addPlayer(new Player(1, 100, 300, 0));
        Player p2 = level.addPlayer(new Player(2, 200, 300, 180));
        p1.move(1, 0, 1f, level);
        assertEquals(2 * Player.RADIUS, p1.pos.dst(p2.pos), DELTA);
        assertEquals(200, p2.pos.x, DELTA);
    }
}
