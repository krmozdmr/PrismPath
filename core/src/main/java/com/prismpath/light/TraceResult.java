package com.prismpath.light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ergebnis einer Strahlberechnung durch den {@code RayTracer}.
 *
 * <ul>
 *   <li>{@link #truncated}: eine feste Grenze wurde erreicht (64 Umlenkungen
 *       pro Strahl oder 512 Strahlen), es fehlen also Strahlstücke.</li>
 *   <li>{@link #unstable}: die Mixer haben sich nach 10 Durchläufen noch nicht
 *       auf ein Ergebnis geeinigt (Frage 6).</li>
 * </ul>
 */
public final class TraceResult {

    public static final TraceResult EMPTY = new TraceResult(List.of(), false, false);

    public final List<BeamSegment> beams;
    public final boolean truncated;
    public final boolean unstable;

    public TraceResult(List<BeamSegment> beams, boolean truncated, boolean unstable) {
        this.beams = Collections.unmodifiableList(new ArrayList<>(beams));
        this.truncated = truncated;
        this.unstable = unstable;
    }
}
