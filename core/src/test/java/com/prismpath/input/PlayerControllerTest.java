package com.prismpath.input;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prismpath.light.LightColor;
import com.prismpath.light.LightSource;
import com.prismpath.light.Mirror;
import com.prismpath.model.Level;
import com.prismpath.model.Player;
import org.junit.jupiter.api.Test;

class PlayerControllerTest {

    private static final float DELTA = 1e-3f;
    private final Level level = new Level(800, 600);
    private final Player p1 = level.addPlayer(new Player(1, 100, 300, 0));
    private final Player p2 = level.addPlayer(new Player(2, 600, 300, 180));
    private final FakeKeyInput keys = new FakeKeyInput();
    private final PlayerController controller = new PlayerController();

    @Test
    void jedeFigurFolgtNurIhrenTasten() {
        keys.press(1, Action.RIGHT).press(2, Action.UP);
        controller.update(level, keys, 0.5f);
        assertEquals(100 + Player.SPEED * 0.5f, p1.pos.x, DELTA);
        assertEquals(300, p1.pos.y, DELTA);
        assertEquals(600, p2.pos.x, DELTA);
        assertEquals(300 + Player.SPEED * 0.5f, p2.pos.y, DELTA);
    }

    @Test
    void gegensaetzlicheTastenHebenSichAuf() {
        keys.press(1, Action.LEFT).press(1, Action.RIGHT);
        controller.update(level, keys, 0.5f);
        assertEquals(100, p1.pos.x, DELTA);
    }

    @Test
    void greifenNurBeimDruecken() {
        Mirror m = level.addObject(new Mirror(140, 300, 45, true, true));
        keys.press(1, Action.GRAB);
        controller.update(level, keys, 0.016f);
        assertSame(m, p1.held);
        controller.update(level, keys, 0.016f);
        assertSame(m, p1.held, "gehaltene Taste legt nicht sofort wieder ab");
        keys.release(1, Action.GRAB);
        controller.update(level, keys, 0.016f);
        keys.press(1, Action.GRAB);
        controller.update(level, keys, 0.016f);
        assertFalse(p1.isHolding());
    }

    @Test
    void drehenBeschleunigtVon20Auf120GradProSekunde() {
        Mirror m = level.addObject(new Mirror(140, 300, 0, true, true));
        keys.press(1, Action.GRAB);
        controller.update(level, keys, 0);
        keys.press(1, Action.ROTATE_LEFT);
        controller.update(level, keys, 0.1f);
        assertEquals(PlayerController.MIN_SPEED * 0.1f, m.angle, DELTA);
        for (int i = 0; i < 20; i++) {
            controller.update(level, keys, 0.1f);
        }
        float before = m.angle;
        controller.update(level, keys, 0.1f);
        assertEquals(PlayerController.MAX_SPEED * 0.1f, m.angle - before, DELTA);
    }

    @Test
    void shiftDrehtFeinImUhrzeigersinn() {
        Mirror m = level.addObject(new Mirror(140, 300, 90, true, true));
        keys.press(1, Action.GRAB);
        controller.update(level, keys, 0);
        keys.press(1, Action.ROTATE_RIGHT).press(1, Action.FINE);
        for (int i = 0; i < 30; i++) {
            controller.update(level, keys, 0.1f);
        }
        assertEquals(90 - PlayerController.FINE_SPEED * 3f, m.angle, DELTA);
    }

    @Test
    void loslassenSetztBeschleunigungZurueck() {
        Mirror m = level.addObject(new Mirror(140, 300, 0, true, true));
        keys.press(1, Action.GRAB);
        controller.update(level, keys, 0);
        keys.press(1, Action.ROTATE_LEFT);
        for (int i = 0; i < 20; i++) {
            controller.update(level, keys, 0.1f);
        }
        keys.release(1, Action.ROTATE_LEFT);
        controller.update(level, keys, 0.1f);
        keys.press(1, Action.ROTATE_LEFT);
        float before = m.angle;
        controller.update(level, keys, 0.1f);
        assertEquals(PlayerController.MIN_SPEED * 0.1f, m.angle - before, DELTA);
        assertTrue(p1.isHolding());
    }

    @Test
    void drehtObjektVorDerFigurAuchOhneTragen() {
        Mirror m = level.addObject(new Mirror(140, 300, 0, true, true));
        keys.press(1, Action.ROTATE_LEFT).press(1, Action.FINE);
        controller.update(level, keys, 1f);
        assertEquals(PlayerController.FINE_SPEED, m.angle, DELTA);
        assertFalse(p1.isHolding());
    }

    @Test
    void lichtquelleDrehtSichNicht() {
        LightSource s = level.addObject(new LightSource(140, 300, 0, LightColor.RED, false, false));
        keys.press(1, Action.ROTATE_LEFT);
        controller.update(level, keys, 0.5f);
        assertEquals(0, s.angle, DELTA);
    }
}
