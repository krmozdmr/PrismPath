package com.prismpath.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LevelTest {

    private final Level level = new Level(800, 600);
    private final Wall wall = level.addWall(new Wall(0, 0, 800, 20));
    private final GameObject box = level.addObject(new GameObjectTest.Box(100, 100, 0, true, true));
    private final Player p1 = level.addPlayer(new Player(1, 200, 200, 0));
    private final Player p2 = level.addPlayer(new Player(2, 300, 200, 180));

    @Test
    void ungueltigeGroesseWirdAbgelehnt() {
        assertThrows(IllegalArgumentException.class, () -> new Level(0, 600));
    }

    @Test
    void solidObjectsEnthaeltWaendeUndObjekte() {
        List<Solid> solids = level.solidObjects();
        assertTrue(solids.contains(wall));
        assertTrue(solids.contains(box));
        assertFalse(solids.contains(p1));
    }

    @Test
    void getragenesObjektIstNichtSolid() {
        p1.held = box;
        assertTrue(level.isHeld(box));
        assertFalse(level.solidObjects().contains(box));
    }

    @Test
    void andereFigurBlockiertAberNichtDieEigene() {
        List<Solid> solids = level.solidsFor(p1);
        assertTrue(solids.contains(p2));
        assertFalse(solids.contains(p1));
    }

    @Test
    void listenSindVonAussenNichtAenderbar() {
        assertThrows(UnsupportedOperationException.class, () -> level.objects().clear());
    }
}
