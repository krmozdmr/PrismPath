package com.prismpath.light;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class TraceResultTest {

    @Test
    void segmentKopiertStartUndEnde() {
        Vector2 start = new Vector2(0, 0);
        Vector2 end = new Vector2(3, 4);
        BeamSegment s = new BeamSegment(start, end, LightColor.RED);
        start.set(100, 100);
        assertEquals(new Vector2(0, 0), s.start);
        assertEquals(5, s.length(), 1e-5f);
    }

    @Test
    void segmentBrauchtEineFarbe() {
        assertThrows(NullPointerException.class,
            () -> new BeamSegment(new Vector2(), new Vector2(1, 0), null));
    }

    @Test
    void ergebnisIstUnveraenderlich() {
        List<BeamSegment> beams = new ArrayList<>();
        beams.add(new BeamSegment(new Vector2(), new Vector2(1, 0), LightColor.WHITE));
        TraceResult r = new TraceResult(beams, true, false);
        beams.clear();
        assertEquals(1, r.beams.size());
        assertThrows(UnsupportedOperationException.class, () -> r.beams.clear());
        assertTrue(r.truncated);
        assertFalse(r.unstable);
    }

    @Test
    void leeresErgebnis() {
        assertTrue(TraceResult.EMPTY.beams.isEmpty());
        assertFalse(TraceResult.EMPTY.truncated);
        assertFalse(TraceResult.EMPTY.unstable);
    }
}
