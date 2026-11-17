package com.prismpath.light;

import com.badlogic.gdx.math.Vector2;
import com.prismpath.model.GameObject;

/**
 * Beidseitiger, gerader Spiegel. Die Spiegelfläche liegt entlang der
 * Blickrichtung {@link #direction()} und reicht {@link #HALF_LENGTH} in
 * beide Richtungen um die Position.
 */
public final class Mirror extends GameObject {

    public static final float HALF_LENGTH = 24f;

    public Mirror(float x, float y, float angle, boolean movable, boolean rotatable) {
        super(x, y, angle, HALF_LENGTH, movable, rotatable);
    }

    /** Die beiden Endpunkte der Spiegelfläche. */
    public Vector2[] endpoints() {
        Vector2 half = direction().scl(HALF_LENGTH);
        return new Vector2[] {pos.cpy().sub(half), pos.cpy().add(half)};
    }

    /** Spiegelt die Richtung {@code dir} an der Spiegelfläche. */
    public Vector2 reflect(Vector2 dir) {
        Vector2 normal = direction().rotate90(1);
        float dot = dir.dot(normal);
        return new Vector2(dir).sub(normal.scl(2 * dot)).nor();
    }
}
