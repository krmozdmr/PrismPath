package com.prismpath.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prismpath.light.RayTracer;
import com.prismpath.light.TraceResult;
import com.prismpath.model.GameObject;
import com.prismpath.model.Level;
import org.junit.jupiter.api.Test;

/** Prüft die mitgelieferten Level: sie laden, starten ungelöst mit geschlossener Tür und sind lösbar. */
class LevelCatalogTest {

    private final LevelCatalog catalog = LevelCatalog.load();
    private final RayTracer tracer = new RayTracer();

    @Test
    void levelInDerReihenfolgeDesIndex() {
        assertEquals(3, catalog.size());
        assertEquals("Erstes Licht", catalog.name(0));
        assertEquals("Regenbogen", catalog.name(1));
        assertEquals("Mischpult", catalog.name(2));
    }

    @Test
    void jedesLevelStartetUngeloestUndOhneGrenzfehler() {
        for (int i = 0; i < catalog.size(); i++) {
            Level level = catalog.create(i);
            TraceResult r = tracer.trace(level);
            assertFalse(level.isSolved(), catalog.file(i) + " ist schon gelöst");
            assertFalse(r.truncated || r.unstable, catalog.file(i));
            assertEquals(2, level.players().size(), catalog.file(i));
            assertNotNull(level.door(), catalog.file(i) + " hat keine Tür");
            assertFalse(level.isDoorOpen(), catalog.file(i));
        }
    }

    @Test
    void createLiefertJedesMalEinFrischesLevel() {
        Level a = catalog.create(0);
        a.objects().get(1).pos.set(1, 1);
        Level b = catalog.create(0);
        assertNotSame(a, b);
        assertEquals(400, b.objects().get(1).pos.x);
    }

    @Test
    void erstesLichtIstLoesbar() {
        Level level = catalog.create(0);
        place(level, 1, 1000, 600, 135); // Rot nach unten zum Ziel
        assertSolved(level);
    }

    @Test
    void regenbogenIstLoesbar() {
        Level level = catalog.create(1);
        place(level, 2, 600, 360, 45);        // Grün nach oben
        place(level, 3, 600, 560, 45);        // Grün nach rechts über die Wand
        place(level, 4, 500, 294.5f, 96.5f);  // Rot nach links unten
        assertSolved(level);
    }

    @Test
    void mischpultIstLoesbar() {
        Level level = catalog.create(2);
        place(level, 2, 640, 360, 0);   // Mixer schickt Gelb nach rechts
        place(level, 3, 640, 520, 135); // Grün (oben) nach unten in den Mixer
        place(level, 4, 640, 200, 45);  // Rot (unten) nach oben in den Mixer
        assertSolved(level);
    }

    private void assertSolved(Level level) {
        tracer.trace(level);
        assertTrue(level.isSolved(), level.name + " soll gelöst sein");
    }

    private static void place(Level level, int index, float x, float y, float angle) {
        GameObject o = level.objects().get(index);
        o.pos.set(x, y);
        o.angle = angle;
    }
}
