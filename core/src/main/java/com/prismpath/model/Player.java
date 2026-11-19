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

    /** Abstand zwischen Figur und Objekt, in dem man es noch greifen kann. */
    public static final float REACH = 24f;
    /** Ein Objekt muss höchstens so viele Grad neben der Blickrichtung liegen. */
    public static final float GRAB_ANGLE = 70f;
    /** Lücke zwischen Figur und getragenem Objekt. */
    private static final float CARRY_GAP = 2f;

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
        updateHeld();
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

    /**
     * Nimmt das nächste bewegliche Objekt vor der Figur auf.
     *
     * @return {@code true}, wenn jetzt ein Objekt getragen wird
     */
    public boolean pickUp(Level level) {
        if (held != null) {
            return false;
        }
        GameObject best = null;
        float bestDist = Float.MAX_VALUE;
        for (GameObject o : level.objects()) {
            if (!o.movable || level.isHeld(o)) {
                continue;
            }
            float gap = pos.dst(o.pos) - RADIUS - o.radius;
            float toObject = MathUtils.atan2(o.pos.y - pos.y, o.pos.x - pos.x) * MathUtils.radiansToDegrees;
            if (gap <= REACH && angleBetween(facing, toObject) <= GRAB_ANGLE && gap < bestDist) {
                best = o;
                bestDist = gap;
            }
        }
        if (best == null) {
            return false;
        }
        held = best;
        updateHeld();
        return true;
    }

    /**
     * Legt das getragene Objekt vor der Figur ab. Liegt dort ein Hindernis,
     * bleibt das Objekt in der Hand.
     *
     * @return {@code true}, wenn das Objekt abgelegt wurde
     */
    public boolean drop(Level level) {
        if (held == null) {
            return false;
        }
        updateHeld();
        if (!fitsAt(held, level)) {
            return false;
        }
        held = null;
        return true;
    }

    /** Dreht das getragene Objekt, falls es drehbar ist. */
    public boolean rotateHeld(float degrees) {
        return held != null && held.rotate(degrees);
    }

    /** Setzt das getragene Objekt vor die Figur in Blickrichtung. */
    public void updateHeld() {
        if (held == null) {
            return;
        }
        float distance = RADIUS + held.radius + CARRY_GAP;
        held.pos.set(pos).add(new Vector2(distance, 0).setAngleDeg(facing));
    }

    private boolean fitsAt(GameObject object, Level level) {
        Vector2 c = object.pos;
        float r = object.radius;
        if (c.x < r || c.y < r || c.x > level.width - r || c.y > level.height - r) {
            return false;
        }
        for (Wall w : level.walls()) {
            if (Collision.overlaps(c, r, w.rect)) {
                return false;
            }
        }
        for (GameObject o : level.objects()) {
            if (o != object && !level.isHeld(o) && Collision.overlaps(c, r, o.pos, o.radius)) {
                return false;
            }
        }
        for (Player p : level.players()) {
            if (Collision.overlaps(c, r, p.pos, RADIUS)) {
                return false;
            }
        }
        return true;
    }

    private static float angleBetween(float a, float b) {
        float d = Math.abs(GameObject.normalize(a) - GameObject.normalize(b));
        return d > 180f ? 360f - d : d;
    }

    @Override
    public boolean pushOut(Vector2 center, float radius) {
        return Collision.pushOut(center, radius, pos, RADIUS);
    }
}
