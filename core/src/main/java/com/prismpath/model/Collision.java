package com.prismpath.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Kollisionsprüfung für freie Bewegung: Figuren und Objekte sind Kreise,
 * Wände sind achsenparallele Rechtecke.
 *
 * <p>Die Methoden {@code pushOut...} schieben einen Kreis aus dem Hindernis
 * heraus. Weil nur der überlappende Anteil entfernt wird, gleitet eine Figur
 * an Wänden entlang, statt stehen zu bleiben.
 */
public final class Collision {

    private static final float EPSILON = 1e-6f;

    private Collision() {
    }

    /** {@code true}, wenn sich Kreis und Rechteck überlappen (Berühren zählt nicht). */
    public static boolean overlaps(Vector2 center, float radius, Rectangle rect) {
        float nx = MathUtils.clamp(center.x, rect.x, rect.x + rect.width);
        float ny = MathUtils.clamp(center.y, rect.y, rect.y + rect.height);
        float dx = center.x - nx;
        float dy = center.y - ny;
        return dx * dx + dy * dy < radius * radius;
    }

    /** {@code true}, wenn sich zwei Kreise überlappen (Berühren zählt nicht). */
    public static boolean overlaps(Vector2 a, float ra, Vector2 b, float rb) {
        float r = ra + rb;
        return a.dst2(b) < r * r;
    }

    /**
     * Schiebt den Kreis aus dem Rechteck heraus.
     *
     * @return {@code true}, wenn der Kreis verschoben wurde
     */
    public static boolean pushOut(Vector2 center, float radius, Rectangle rect) {
        float nx = MathUtils.clamp(center.x, rect.x, rect.x + rect.width);
        float ny = MathUtils.clamp(center.y, rect.y, rect.y + rect.height);
        float dx = center.x - nx;
        float dy = center.y - ny;
        float dist2 = dx * dx + dy * dy;
        if (dist2 >= radius * radius) {
            return false;
        }
        if (dist2 > EPSILON) {
            float dist = (float) Math.sqrt(dist2);
            float depth = radius - dist;
            center.add(dx / dist * depth, dy / dist * depth);
            return true;
        }
        // Mittelpunkt liegt im Rechteck: über die nächste Kante hinausschieben.
        float left = center.x - rect.x;
        float right = rect.x + rect.width - center.x;
        float bottom = center.y - rect.y;
        float top = rect.y + rect.height - center.y;
        float min = Math.min(Math.min(left, right), Math.min(bottom, top));
        if (min == left) {
            center.x = rect.x - radius;
        } else if (min == right) {
            center.x = rect.x + rect.width + radius;
        } else if (min == bottom) {
            center.y = rect.y - radius;
        } else {
            center.y = rect.y + rect.height + radius;
        }
        return true;
    }

    /**
     * Schiebt Kreis {@code a} aus dem festen Kreis {@code b} heraus.
     *
     * @return {@code true}, wenn {@code a} verschoben wurde
     */
    public static boolean pushOut(Vector2 a, float ra, Vector2 b, float rb) {
        float r = ra + rb;
        float dist2 = a.dst2(b);
        if (dist2 >= r * r) {
            return false;
        }
        if (dist2 > EPSILON) {
            float dist = (float) Math.sqrt(dist2);
            float depth = r - dist;
            a.add((a.x - b.x) / dist * depth, (a.y - b.y) / dist * depth);
        } else {
            a.x = b.x + r; // genau übereinander: in eine feste Richtung lösen
        }
        return true;
    }
}
