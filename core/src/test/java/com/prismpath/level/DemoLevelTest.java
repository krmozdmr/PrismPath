package com.prismpath.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prismpath.light.Mirror;
import com.prismpath.light.RayTracer;
import com.prismpath.light.Target;
import com.prismpath.light.TraceResult;
import com.prismpath.model.GameObject;
import com.prismpath.model.Level;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DemoLevelTest {

    private final RayTracer tracer = new RayTracer();

    private static <T> List<T> all(Level level, Class<T> type) {
        return level.objects().stream().filter(type::isInstance).map(type::cast)
            .collect(Collectors.toList());
    }

    @Test
    void startzustandIstNichtGeloest() {
        Level level = DemoLevel.create();
        TraceResult r = tracer.trace(level);
        assertFalse(level.isSolved());
        assertFalse(r.truncated);
        assertEquals(3, all(level, Target.class).size());
        assertEquals(2, level.players().size());
    }

    @Test
    void levelIstMitDenDreiSpiegelnLoesbar() {
        Level level = DemoLevel.create();
        List<Mirror> mirrors = all(level, Mirror.class);
        place(mirrors.get(0), 600, 360, 45);      // Grün nach oben
        place(mirrors.get(1), 600, 560, 45);      // Grün nach rechts über die Wand
        place(mirrors.get(2), 500, 294.5f, 96.5f); // Rot nach links unten
        tracer.trace(level);
        for (Target t : all(level, Target.class)) {
            assertTrue(t.lit, t.color + " soll leuchten");
        }
        assertTrue(level.isSolved());
    }

    private static void place(GameObject o, float x, float y, float angle) {
        o.pos.set(x, y);
        o.angle = angle;
    }
}
