package com.prismpath.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.List;

/**
 * Spielfigur in Draufsicht. Die Figur ist für die Kollision ein Kreis und
 * kann höchstens ein Objekt tragen ({@link #held}).
 */
public final class Player implements Solid {

    public static final float RADIUS = 16f;
    /** Laufgeschwindigkeit in Einheiten pro Sekunde. */
    public static final float SPEED = 200f;

    /** Höchstens so weit pro Teilschritt, damit niemand durch dünne Wände läuft. */
    private static final float MAX_STEP = RADIUS / 2f;
    private static final int RESOLVE_PASSES = 4;

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

    /**
     * Bewegt die Figur frei in Richtung ({@code dirX}, {@code dirY}).
     * Längere Richtungsvektoren werden auf Länge 1 gekürzt, schräg ist man also
     * nicht schneller. Hindernisse schieben die Figur zurück; dadurch gleitet
     * sie an Wänden entlang. Die Figur bleibt immer im Level.
     */
    public void move(float dirX, float dirY, float dt, Level level) {
        float len = (float) Math.sqrt(dirX * dirX + dirY * dirY);
        if (len < 1e-6f || dt <= 0) {
            return;
        }
        if (len > 1f) {
            dirX /= len;
            dirY /= len;
        }
        facing = GameObject.normalize(MathUtils.atan2(dirY, dirX) * MathUtils.radiansToDegrees);

        float dx = dirX * SPEED * dt;
        float dy = dirY * SPEED * dt;
        int steps = Math.max(1, (int) Math.ceil(Math.sqrt(dx * dx + dy * dy) / MAX_STEP));
        List<Solid> solids = level.solidsFor(this);
        for (int i = 0; i < steps; i++) {
            pos.add(dx / steps, dy / steps);
            resolve(solids, level);
        }
    }

    private void resolve(List<Solid> solids, Level level) {
        for (int pass = 0; pass < RESOLVE_PASSES; pass++) {
            boolean moved = false;
            for (Solid s : solids) {
                moved |= s.pushOut(pos, RADIUS);
            }
            pos.x = MathUtils.clamp(pos.x, RADIUS, level.width - RADIUS);
            pos.y = MathUtils.clamp(pos.y, RADIUS, level.height - RADIUS);
            if (!moved) {
                return;
            }
        }
    }

    @Override
    public boolean pushOut(Vector2 center, float radius) {
        return Collision.pushOut(center, radius, pos, RADIUS);
    }
}
