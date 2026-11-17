package com.prismpath.light;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * Schnittpunkte eines Strahls {@code origin + t · dir} ({@code dir} mit Länge 1)
 * mit einfachen Formen. Alle Methoden liefern den Abstand {@code t} oder
 * {@link #MISS}. Liegt der Startpunkt in der Form, ist {@code t = 0}.
 */
final class RayMath {

    static final float MISS = Float.POSITIVE_INFINITY;
    /** Treffer näher als dieser Abstand zählen nicht (Startpunkt auf einer Fläche). */
    static final float EPSILON = 1e-3f;

    private RayMath() {
    }

    static float circle(Vector2 origin, Vector2 dir, Vector2 center, float radius) {
        float fx = origin.x - center.x;
        float fy = origin.y - center.y;
        float c = fx * fx + fy * fy - radius * radius;
        if (c < 0) {
            return 0;
        }
        float b = fx * dir.x + fy * dir.y;
        float disc = b * b - c;
        if (b > 0 || disc < 0) {
            return MISS;
        }
        float t = -b - (float) Math.sqrt(disc);
        return t > EPSILON ? t : MISS;
    }

    static float rect(Vector2 origin, Vector2 dir, Rectangle r) {
        float tMin = Float.NEGATIVE_INFINITY;
        float tMax = Float.POSITIVE_INFINITY;
        float[] o = {origin.x, origin.y};
        float[] d = {dir.x, dir.y};
        float[] min = {r.x, r.y};
        float[] max = {r.x + r.width, r.y + r.height};
        for (int i = 0; i < 2; i++) {
            if (Math.abs(d[i]) < 1e-9f) {
                if (o[i] < min[i] || o[i] > max[i]) {
                    return MISS;
                }
                continue;
            }
            float t1 = (min[i] - o[i]) / d[i];
            float t2 = (max[i] - o[i]) / d[i];
            tMin = Math.max(tMin, Math.min(t1, t2));
            tMax = Math.min(tMax, Math.max(t1, t2));
        }
        if (tMin > tMax || tMax < EPSILON) {
            return MISS;
        }
        return tMin > EPSILON ? tMin : 0;
    }

    static float segment(Vector2 origin, Vector2 dir, Vector2 a, Vector2 b) {
        float ex = b.x - a.x;
        float ey = b.y - a.y;
        float denom = dir.x * ey - dir.y * ex;
        if (Math.abs(denom) < 1e-9f) {
            return MISS;
        }
        float ax = a.x - origin.x;
        float ay = a.y - origin.y;
        float t = (ax * ey - ay * ex) / denom;
        float s = (ax * dir.y - ay * dir.x) / denom;
        return t > EPSILON && s >= 0 && s <= 1 ? t : MISS;
    }

    /** Abstand bis zum Rand der Fläche (0, 0)–(width, height); 0, wenn außerhalb. */
    static float exit(Vector2 origin, Vector2 dir, float width, float height) {
        if (origin.x < 0 || origin.y < 0 || origin.x > width || origin.y > height) {
            return 0;
        }
        float tx = dir.x > 0 ? (width - origin.x) / dir.x
            : dir.x < 0 ? -origin.x / dir.x : MISS;
        float ty = dir.y > 0 ? (height - origin.y) / dir.y
            : dir.y < 0 ? -origin.y / dir.y : MISS;
        return Math.min(tx, ty);
    }
}
