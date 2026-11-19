package com.prismpath.light;

import com.badlogic.gdx.math.Vector2;
import java.util.Objects;

/**
 * Ein gerades Strahlstück mit Start, Ende und Farbe.
 *
 * <p>Start und Ende werden beim Erzeugen kopiert. Die Vektoren gehören dem
 * Segment und dürfen von außen nicht verändert werden.
 */
public final class BeamSegment {

    public final Vector2 start;
    public final Vector2 end;
    public final LightColor color;

    public BeamSegment(Vector2 start, Vector2 end, LightColor color) {
        this.start = new Vector2(Objects.requireNonNull(start, "start"));
        this.end = new Vector2(Objects.requireNonNull(end, "end"));
        this.color = Objects.requireNonNull(color, "color");
    }

    public float length() {
        return start.dst(end);
    }

    @Override
    public String toString() {
        return color + " " + start + " -> " + end;
    }
}
