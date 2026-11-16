package com.prismpath.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Spielfigur in Draufsicht. Die Figur ist für die Kollision ein Kreis und
 * kann höchstens ein Objekt tragen ({@link #held}).
 */
public final class Player implements Solid {

    public static final float RADIUS = 16f;

    public final int id;
    public final Vector2 pos;
    /** Blickrichtung in Grad, 0° zeigt nach rechts. */
    public float facing;
    /** Getragenes Objekt oder {@code null}. */
    public GameObject held;

    public Player(int id, float x, float y, float facing) {
        this.id = id;
        this.pos = new Vector2(x, y);
        this.facing = GameObject.normalize(facing);
    }

    public boolean isHolding() {
        return held != null;
    }

    @Override
    public boolean pushOut(Vector2 center, float radius) {
        return Collision.pushOut(center, radius, pos, RADIUS);
    }
}
