package com.prismpath.light;

import static com.prismpath.light.LightColor.BLUE;
import static com.prismpath.light.LightColor.CYAN;
import static com.prismpath.light.LightColor.GREEN;
import static com.prismpath.light.LightColor.MAGENTA;
import static com.prismpath.light.LightColor.RED;
import static com.prismpath.light.LightColor.WHITE;
import static com.prismpath.light.LightColor.YELLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class LightColorTest {

    @Test
    void esGibtGenauSiebenFarben() {
        assertEquals(7, LightColor.values().length);
    }

    @Test
    void mischregelnAusDemArchitekturentwurf() {
        assertEquals(YELLOW, RED.mix(GREEN));
        assertEquals(CYAN, GREEN.mix(BLUE));
        assertEquals(MAGENTA, RED.mix(BLUE));
        assertEquals(WHITE, RED.mix(GREEN).mix(BLUE));
    }

    @Test
    void mischenIstIdempotentUndKommutativ() {
        assertEquals(RED, RED.mix(RED));
        assertEquals(BLUE.mix(YELLOW), YELLOW.mix(BLUE));
        assertEquals(WHITE, WHITE.mix(GREEN));
    }

    @Test
    void maskeHinUndZurueck() {
        for (LightColor c : LightColor.values()) {
            assertEquals(c, LightColor.fromMask(c.mask));
        }
    }

    @Test
    void ungueltigeMaskenWerdenAbgelehnt() {
        assertThrows(IllegalArgumentException.class, () -> LightColor.fromMask(0));
        assertThrows(IllegalArgumentException.class, () -> LightColor.fromMask(8));
    }

    @Test
    void nurRotGruenBlauSindGrundfarben() {
        assertTrue(RED.isPrimary());
        assertTrue(GREEN.isPrimary());
        assertTrue(BLUE.isPrimary());
        assertFalse(YELLOW.isPrimary());
        assertFalse(WHITE.isPrimary());
    }

    @Test
    void zerlegungInGrundfarben() {
        assertEquals(List.of(RED, GREEN, BLUE), WHITE.components());
        assertEquals(List.of(RED, BLUE), MAGENTA.components());
        assertEquals(List.of(GREEN), GREEN.components());
    }

    @Test
    void enthaeltPrueftAlleAnteile() {
        assertTrue(CYAN.contains(BLUE));
        assertFalse(CYAN.contains(RED));
        assertFalse(YELLOW.contains(WHITE));
    }
}
