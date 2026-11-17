package com.prismpath.light;

import com.prismpath.model.GameObject;
import java.util.Objects;

/** Sendet einen Lichtstrahl in Blickrichtung aus. */
public final class LightSource extends GameObject {

    public static final float RADIUS = 14f;

    public final LightColor color;

    public LightSource(float x, float y, float angle, LightColor color,
                       boolean movable, boolean rotatable) {
        super(x, y, angle, RADIUS, movable, rotatable);
        this.color = Objects.requireNonNull(color, "color");
    }
}
