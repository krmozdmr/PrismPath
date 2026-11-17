package com.prismpath.light;

import static com.prismpath.light.RayTracerTest.DELTA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prismpath.model.Level;
import org.junit.jupiter.api.Test;

class RayTracerMirrorTest {

    private final Level level = new Level(800, 600);
    private final RayTracer tracer = new RayTracer();

    @Test
    void spiegelLenktDenStrahlUm() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        level.addObject(new Mirror(400, 300, 45, true, true));
        TraceResult r = tracer.trace(level);
        assertEquals(2, r.beams.size());
        assertEquals(400, r.beams.get(0).end.x, DELTA);
        BeamSegment up = r.beams.get(1);
        assertEquals(400, up.end.x, DELTA);
        assertEquals(600, up.end.y, DELTA);
        assertEquals(LightColor.WHITE, up.color);
    }

    @Test
    void zielUeberZweiSpiegel() {
        level.addObject(new LightSource(100, 100, 0, LightColor.BLUE, false, false));
        level.addObject(new Mirror(400, 100, 45, true, true));
        level.addObject(new Mirror(400, 400, 135, true, true));
        Target t = level.addObject(new Target(100, 400, LightColor.BLUE));
        tracer.trace(level);
        assertTrue(t.lit);
    }

    @Test
    void strahlVerfehltSpiegelNebenSeinemEnde() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        level.addObject(new Mirror(400, 300 + Mirror.HALF_LENGTH + 20, 45, true, true));
        assertEquals(1, tracer.trace(level).beams.size());
    }

    @Test
    void lichtquelleBlockiertKeinLicht() {
        level.addObject(new LightSource(100, 300, 0, LightColor.RED, false, false));
        level.addObject(new Mirror(400, 300, 90, true, true));
        Target t = level.addObject(new Target(50, 300, LightColor.RED));
        tracer.trace(level);
        assertTrue(t.lit);
    }

    @Test
    void endlosschleifeEndetNachMaxBounces() {
        level.addObject(new Mirror(300, 300, 90, false, false));
        level.addObject(new Mirror(500, 300, 90, false, false));
        level.addObject(new LightSource(400, 300, 0.01f, LightColor.WHITE, false, false));
        TraceResult r = tracer.trace(level);
        assertTrue(r.truncated);
        assertEquals(RayTracer.MAX_BOUNCES + 1, r.beams.size());
    }

    @Test
    void getragenerSpiegelReflektiertWeiter() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        Mirror m = level.addObject(new Mirror(400, 300, 45, true, true));
        com.prismpath.model.Player p = level.addPlayer(new com.prismpath.model.Player(1, 400, 260, 90));
        assertTrue(p.pickUp(level));
        TraceResult r = tracer.trace(level);
        assertEquals(2, r.beams.size());
        assertFalse(r.truncated);
        // Der Spiegel liegt jetzt vor der Figur; unter 45° trifft der Strahl ihn
        // dort, wo die Spiegelfläche die Höhe y = 300 kreuzt.
        assertEquals(m.pos.x - (m.pos.y - 300), r.beams.get(0).end.x, DELTA);
    }
}
