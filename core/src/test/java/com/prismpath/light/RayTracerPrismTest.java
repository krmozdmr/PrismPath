package com.prismpath.light;

import static com.prismpath.light.RayTracerTest.DELTA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import com.prismpath.model.Level;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class RayTracerPrismTest {

    private final Level level = new Level(800, 600);
    private final RayTracer tracer = new RayTracer();

    private List<BeamSegment> leaving(TraceResult r, Prism prism) {
        return r.beams.stream().filter(b -> b.start.epsilonEquals(prism.pos, 1e-3f))
            .collect(Collectors.toList());
    }

    private static float angleOf(BeamSegment b) {
        return new Vector2(b.end).sub(b.start).angleDeg();
    }

    @Test
    void weissWirdInRotGruenBlauZerlegt() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        Prism prism = level.addObject(new Prism(300, 300, 0, true, true));
        TraceResult r = tracer.trace(level);
        assertEquals(prism.pos, r.beams.get(0).end);
        List<BeamSegment> out = leaving(r, prism);
        assertEquals(3, out.size());
        assertEquals(LightColor.RED, out.get(0).color);
        assertEquals(340, angleOf(out.get(0)), DELTA);
        assertEquals(LightColor.GREEN, out.get(1).color);
        assertEquals(0, angleOf(out.get(1)), DELTA);
        assertEquals(LightColor.BLUE, out.get(2).color);
        assertEquals(20, angleOf(out.get(2)), DELTA);
    }

    @Test
    void grundfarbeGehtGeradeDurch() {
        level.addObject(new LightSource(100, 300, 0, LightColor.RED, false, false));
        Prism prism = level.addObject(new Prism(300, 300, 30, true, true));
        List<BeamSegment> out = leaving(tracer.trace(level), prism);
        assertEquals(1, out.size());
        assertEquals(LightColor.RED, out.get(0).color);
        assertEquals(0, angleOf(out.get(0)), DELTA);
    }

    @Test
    void gelbWirdNurInRotUndGruenZerlegt() {
        level.addObject(new LightSource(100, 300, 0, LightColor.YELLOW, false, false));
        Prism prism = level.addObject(new Prism(300, 300, 0, true, true));
        List<BeamSegment> out = leaving(tracer.trace(level), prism);
        assertEquals(2, out.size());
        assertEquals(LightColor.RED, out.get(0).color);
        assertEquals(LightColor.GREEN, out.get(1).color);
    }

    @Test
    void drehenDesPrismasLenktDieStrahlen() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        Prism prism = level.addObject(new Prism(300, 300, 30, true, true));
        List<BeamSegment> out = leaving(tracer.trace(level), prism);
        assertEquals(Prism.MAX_STEER, angleOf(out.get(1)), DELTA);
    }

    @Test
    void zerlegtesLichtErreichtFarbigeZiele() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        level.addObject(new Prism(300, 300, 0, true, true));
        Vector2 green = new Vector2(600, 300);
        Vector2 blue = new Vector2(1, 0).setAngleDeg(20).scl(300).add(300, 300);
        Target g = level.addObject(new Target(green.x, green.y, LightColor.GREEN));
        Target b = level.addObject(new Target(blue.x, blue.y, LightColor.BLUE));
        tracer.trace(level);
        assertTrue(g.lit);
        assertTrue(b.lit);
    }

    @Test
    void zweitesPrismaZerlegtGrundfarbenNichtWeiter() {
        level.addObject(new LightSource(100, 300, 0, LightColor.WHITE, false, false));
        level.addObject(new Prism(300, 300, 0, true, true));
        Prism second = level.addObject(new Prism(500, 300, 0, true, true));
        List<BeamSegment> out = leaving(tracer.trace(level), second);
        assertEquals(1, out.size());
        assertEquals(LightColor.GREEN, out.get(0).color);
    }
}
