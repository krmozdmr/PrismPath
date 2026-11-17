package com.prismpath.light;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prismpath.model.Level;
import com.prismpath.model.Wall;
import org.junit.jupiter.api.Test;

class RayTracerTest {

    static final float DELTA = 1e-2f;
    final Level level = new Level(800, 600);
    final RayTracer tracer = new RayTracer();

    LightSource source(float x, float y, float angle, LightColor color) {
        return level.addObject(new LightSource(x, y, angle, color, false, false));
    }

    @Test
    void ohneLichtquelleKeineStrahlen() {
        assertTrue(tracer.trace(level).beams.isEmpty());
    }

    @Test
    void strahlStartetAmRandDerQuelleUndEndetAmLevelrand() {
        source(100, 300, 0, LightColor.WHITE);
        TraceResult r = tracer.trace(level);
        assertEquals(1, r.beams.size());
        BeamSegment b = r.beams.get(0);
        assertEquals(100 + LightSource.RADIUS, b.start.x, DELTA);
        assertEquals(800, b.end.x, DELTA);
        assertEquals(300, b.end.y, DELTA);
        assertEquals(LightColor.WHITE, b.color);
        assertFalse(r.truncated);
    }

    @Test
    void wandStopptDenStrahl() {
        source(100, 300, 0, LightColor.RED);
        level.addWall(new Wall(400, 200, 20, 200));
        BeamSegment b = tracer.trace(level).beams.get(0);
        assertEquals(400, b.end.x, DELTA);
    }

    @Test
    void zielInPassenderFarbeLeuchtet() {
        source(100, 300, 0, LightColor.RED);
        Target t = level.addObject(new Target(500, 300, LightColor.RED));
        TraceResult r = tracer.trace(level);
        assertTrue(t.lit);
        assertTrue(level.isSolved());
        assertEquals(500 - Target.RADIUS, r.beams.get(0).end.x, DELTA);
    }

    @Test
    void zielInFalscherFarbeBleibtDunkel() {
        source(100, 300, 0, LightColor.WHITE);
        Target t = level.addObject(new Target(500, 300, LightColor.RED));
        tracer.trace(level);
        assertFalse(t.lit);
        assertFalse(level.isSolved());
    }

    @Test
    void zielHinterWandBleibtDunkel() {
        source(100, 300, 0, LightColor.RED);
        level.addWall(new Wall(300, 0, 20, 600));
        Target t = level.addObject(new Target(500, 300, LightColor.RED));
        tracer.trace(level);
        assertFalse(t.lit);
    }

    @Test
    void zielErlischtWennDerStrahlFehlt() {
        source(100, 300, 0, LightColor.RED);
        Target t = level.addObject(new Target(500, 300, LightColor.RED));
        tracer.trace(level);
        assertTrue(t.lit);
        t.pos.y = 100;
        tracer.trace(level);
        assertFalse(t.lit);
    }

    @Test
    void jedeQuelleSendetEinenStrahl() {
        source(100, 100, 0, LightColor.RED);
        source(100, 500, 0, LightColor.BLUE);
        assertEquals(2, tracer.trace(level).beams.size());
    }
}
