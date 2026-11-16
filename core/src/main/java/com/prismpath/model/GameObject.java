package com.prismpath.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Abstrakte Basisklasse aller Objekte im Level (Lichtquelle, Spiegel, Prisma,
 * Mixer, Ziel, später Teleporter).
 *
 * <p>Der Winkel ist in Grad angegeben, 0° zeigt nach rechts, gegen den
 * Uhrzeigersinn positiv. Für die Kollision ist jedes Objekt ein Kreis.
 */
public abstract class GameObject implements Solid {

    public final Vector2 pos;
    public float angle;
    public final boolean movable;
    public final boolean rotatable;
    public final float radius;

    protected GameObject(float x, float y, float angle, float radius,
                         boolean movable, boolean rotatable) {
        this.pos = new Vector2(x, y);
        this.angle = normalize(angle);
        this.radius = radius;
        this.movable = movable;
        this.rotatable = rotatable;
    }

    /** Einheitsvektor in Blickrichtung des Objekts. */
    public Vector2 direction() {
        return new Vector2(1, 0).setAngleDeg(angle);
    }

    /**
     * Dreht das Objekt, falls es drehbar ist.
     *
     * @return {@code true}, wenn sich der Winkel geändert hat
     */
    public boolean rotate(float degrees) {
        if (!rotatable || degrees == 0) {
            return false;
        }
        angle = normalize(angle + degrees);
        return true;
    }

    @Override
    public boolean pushOut(Vector2 center, float r) {
        return Collision.pushOut(center, r, pos, radius);
    }

    /** Bringt einen Winkel in den Bereich [0, 360). */
    static float normalize(float degrees) {
        float a = degrees % 360f;
        return a < 0 ? a + 360f : a;
    }
}
