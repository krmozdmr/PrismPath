package com.prismpath.light;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import org.junit.jupiter.api.Test;

class OpticalObjectsTest {

    private static final float DELTA = 1e-4f;

    @Test
    void spiegelEndpunkteLiegenEntlangDesWinkels() {
        Vector2[] e = new Mirror(100, 100, 90, true, true).endpoints();
        assertEquals(100, e[0].x, DELTA);
        assertEquals(100 - Mirror.HALF_LENGTH, e[0].y, DELTA);
        assertEquals(100 + Mirror.HALF_LENGTH, e[1].y, DELTA);
    }

    @Test
    void spiegelUnter45GradLenktRechtsNachOben() {
        Vector2 out = new Mirror(0, 0, 45, true, true).reflect(new Vector2(1, 0));
        assertEquals(0, out.x, DELTA);
        assertEquals(1, out.y, DELTA);
    }

    @Test
    void senkrechterSpiegelWirftZurueck() {
        Vector2 out = new Mirror(0, 0, 90, true, true).reflect(new Vector2(1, 0));
        assertEquals(-1, out.x, DELTA);
        assertEquals(0, out.y, DELTA);
    }

    @Test
    void prismaAblenkungJeGrundfarbe() {
        assertEquals(-20, Prism.offsetFor(LightColor.RED), DELTA);
        assertEquals(0, Prism.offsetFor(LightColor.GREEN), DELTA);
        assertEquals(20, Prism.offsetFor(LightColor.BLUE), DELTA);
        assertThrows(IllegalArgumentException.class, () -> Prism.offsetFor(LightColor.WHITE));
    }

    @Test
    void prismaSteuerungWiederholtSichAlle120Grad() {
        assertEquals(0, new Prism(0, 0, 0, true, true).steer(), DELTA);
        assertEquals(Prism.MAX_STEER, new Prism(0, 0, 30, true, true).steer(), DELTA);
        assertEquals(new Prism(0, 0, 10, true, true).steer(),
            new Prism(0, 0, 130, true, true).steer(), DELTA);
    }

    @Test
    void mixerAddiertFarben() {
        Mixer m = new Mixer(0, 0, 0, Mixer.Type.ADDITIVE, false, true);
        assertNull(m.output(0));
        assertEquals(LightColor.YELLOW, m.output(LightColor.RED.mask | LightColor.GREEN.mask));
    }

    @Test
    void zielAkzeptiertNurGenauSeineFarbe() {
        Target t = new Target(0, 0, LightColor.YELLOW);
        assertTrue(t.accepts(LightColor.YELLOW));
        assertFalse(t.accepts(LightColor.WHITE));
        assertFalse(t.accepts(LightColor.RED));
        assertFalse(t.movable);
    }
}
