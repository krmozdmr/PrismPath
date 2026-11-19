package com.prismpath.light;

import com.prismpath.model.GameObject;
import com.prismpath.model.Goal;
import java.util.Objects;

/**
 * Ziel mit geforderter Farbe. Es leuchtet nur bei Licht in genau seiner Farbe
 * (Frage 5). {@link #lit} setzt der {@code RayTracer} in jedem Frame neu.
 */
public final class Target extends GameObject implements Goal {

    public static final float RADIUS = 16f;

    public final LightColor color;
    public boolean lit;

    public Target(float x, float y, LightColor color) {
        super(x, y, 0, RADIUS, false, false);
        this.color = Objects.requireNonNull(color, "color");
    }

    public boolean accepts(LightColor incoming) {
        return incoming == color;
    }

    @Override
    public boolean isLit() {
        return lit;
    }
}
