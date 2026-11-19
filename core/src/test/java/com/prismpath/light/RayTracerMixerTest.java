package com.prismpath.light;

import static com.prismpath.light.RayTracerTest.DELTA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.prismpath.model.Level;
import org.junit.jupiter.api.Test;

class RayTracerMixerTest {

    private final Level level = new Level(800, 600);
    private final RayTracer tracer = new RayTracer();

    private Mixer mixer(float x, float y, float angle) {
        return level.addObject(new Mixer(x, y, angle, Mixer.Type.ADDITIVE, false, true));
    }

    @Test
    void rotUndGruenErgebenGelb() {
        level.addObject(new LightSource(100, 300, 0, LightColor.RED, false, false));
        level.addObject(new LightSource(400, 100, 90, LightColor.GREEN, false, false));
        mixer(400, 300, 0);
        Target t = level.addObject(new Target(700, 300, LightColor.YELLOW));
        TraceResult r = tracer.trace(level);
        assertTrue(t.lit);
        assertTrue(level.isSolved());
        assertFalse(r.unstable);
    }

    @Test
    void mixerSendetAusDerMitteInBlickrichtung() {
        level.addObject(new LightSource(100, 300, 0, LightColor.BLUE, false, false));
        Mixer m = mixer(400, 300, 90);
        TraceResult r = tracer.trace(level);
        assertEquals(2, r.beams.size());
        assertEquals(m.pos, r.beams.get(0).end);
        BeamSegment out = r.beams.get(1);
        assertEquals(m.pos, out.start);
        assertEquals(600, out.end.y, DELTA);
        assertEquals(LightColor.BLUE, out.color);
    }

    @Test
    void strahlNebenDerMitteKommtTrotzdemAn() {
        level.addObject(new LightSource(100, 290, 0, LightColor.BLUE, false, false));
        Mixer m = mixer(400, 300, 90);
        TraceResult r = tracer.trace(level);
        assertEquals(2, r.beams.size());
        assertTrue(r.beams.get(0).end.epsilonEquals(400, 290, 1e-3f), "kein Knick zur Mitte");
        assertEquals(m.pos, r.beams.get(1).start);
        assertEquals(LightColor.BLUE, r.beams.get(1).color);
    }

    @Test
    void mixerOhneLichtBleibtDunkel() {
        mixer(400, 300, 0);
        TraceResult r = tracer.trace(level);
        assertTrue(r.beams.isEmpty());
        assertFalse(r.unstable);
    }

    @Test
    void alleDreiGrundfarbenErgebenWeiss() {
        level.addObject(new LightSource(100, 300, 0, LightColor.RED, false, false));
        level.addObject(new LightSource(400, 100, 90, LightColor.GREEN, false, false));
        level.addObject(new LightSource(400, 500, 270, LightColor.BLUE, false, false));
        mixer(400, 300, 0);
        Target t = level.addObject(new Target(700, 300, LightColor.WHITE));
        tracer.trace(level);
        assertTrue(t.lit);
    }

    @Test
    void hintereinanderGeschalteteMixer() {
        level.addObject(new LightSource(100, 300, 0, LightColor.RED, false, false));
        level.addObject(new LightSource(300, 100, 90, LightColor.GREEN, false, false));
        mixer(300, 300, 0);
        level.addObject(new LightSource(500, 100, 90, LightColor.BLUE, false, false));
        mixer(500, 300, 0);
        Target t = level.addObject(new Target(700, 300, LightColor.WHITE));
        TraceResult r = tracer.trace(level);
        assertTrue(t.lit);
        assertFalse(r.unstable);
    }

    @Test
    void zuWenigeDurchlaeufeMeldenUnstable() {
        level.addObject(new LightSource(100, 300, 0, LightColor.RED, false, false));
        mixer(400, 300, 0);
        TraceResult r = new RayTracer(RayTracer.MAX_BOUNCES, RayTracer.MAX_RAYS, 1).trace(level);
        assertTrue(r.unstable);
    }

    @Test
    void strahlGrenzeMeldetTruncated() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        level.addObject(new Prism(300, 300, 0, true, true));
        TraceResult r = new RayTracer(RayTracer.MAX_BOUNCES, 3, RayTracer.MAX_MIXER_PASSES).trace(level);
        assertTrue(r.truncated);
        assertEquals(3, r.beams.size());
    }
}
