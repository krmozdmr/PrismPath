package com.prismpath.light;

import com.badlogic.gdx.math.Vector2;
import com.prismpath.model.GameObject;
import com.prismpath.model.Level;
import com.prismpath.model.Wall;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Verfolgt alle Lichtstrahlen durch das Level, setzt {@link Target#lit} und
 * liefert die Strahlstücke als {@link TraceResult}.
 *
 * <p>Lichtquellen blockieren kein Licht. Jedes gerade Strahlstück ist ein Strahl. Feste Grenzen verhindern
 * Endlosschleifen (Frage 6): {@value #MAX_BOUNCES} Umlenkungen pro Strahl
 * und {@value #MAX_RAYS} Strahlen insgesamt.
 */
public final class RayTracer {

    public static final int MAX_BOUNCES = 64;
    public static final int MAX_RAYS = 512;

    /** Ein Strahl, der noch verfolgt werden muss. */
    private static final class Ray {
        final Vector2 origin;
        final Vector2 dir;
        final LightColor color;
        final int bounces;
        /** Objekt, das den Strahl ausgesendet hat und nicht sofort getroffen wird. */
        final GameObject from;

        Ray(Vector2 origin, Vector2 dir, LightColor color, int bounces, GameObject from) {
            this.origin = origin;
            this.dir = dir;
            this.color = color;
            this.bounces = bounces;
            this.from = from;
        }
    }

    /** Nächster Treffer eines Strahls; {@code object == null} für Wand oder Levelrand. */
    private static final class Hit {
        final Vector2 point;
        final GameObject object;

        Hit(Vector2 point, GameObject object) {
            this.point = point;
            this.object = object;
        }
    }

    /** Zwischenstand einer Berechnung. */
    private static final class Pass {
        final List<BeamSegment> beams = new ArrayList<>();
        final Set<Target> lit = new HashSet<>();
        final Deque<Ray> queue = new ArrayDeque<>();
        int rays;
        boolean truncated;
    }

    public TraceResult trace(Level level) {
        Pass pass = runPass(level);
        for (GameObject o : level.objects()) {
            if (o instanceof Target) {
                ((Target) o).lit = pass.lit.contains(o);
            }
        }
        return new TraceResult(pass.beams, pass.truncated, false);
    }

    private Pass runPass(Level level) {
        Pass pass = new Pass();
        for (GameObject o : level.objects()) {
            if (o instanceof LightSource) {
                LightSource s = (LightSource) o;
                Vector2 dir = s.direction();
                Vector2 origin = s.pos.cpy().mulAdd(dir, s.radius);
                emit(pass, origin, dir, s.color, 0, s);
            }
        }
        while (!pass.queue.isEmpty()) {
            follow(level, pass, pass.queue.poll());
        }
        return pass;
    }

    private void emit(Pass pass, Vector2 origin, Vector2 dir, LightColor color, int bounces, GameObject from) {
        if (pass.rays >= MAX_RAYS) {
            pass.truncated = true;
            return;
        }
        pass.rays++;
        pass.queue.add(new Ray(origin, dir, color, bounces, from));
    }

    private void follow(Level level, Pass pass, Ray ray) {
        Hit hit = nearestHit(level, ray);
        pass.beams.add(new BeamSegment(ray.origin, hit.point, ray.color));
        GameObject o = hit.object;
        if (o instanceof Mirror) {
            if (!canBounce(pass, ray)) {
                return;
            }
            emit(pass, hit.point, ((Mirror) o).reflect(ray.dir), ray.color, ray.bounces + 1, o);
        } else if (o instanceof Target) {
            Target t = (Target) o;
            if (t.accepts(ray.color)) {
                pass.lit.add(t);
            }
        }
    }

    private static boolean canBounce(Pass pass, Ray ray) {
        if (ray.bounces >= MAX_BOUNCES) {
            pass.truncated = true;
            return false;
        }
        return true;
    }

    private Hit nearestHit(Level level, Ray ray) {
        float best = RayMath.exit(ray.origin, ray.dir, level.width, level.height);
        GameObject bestObject = null;
        for (Wall w : level.walls()) {
            float t = RayMath.rect(ray.origin, ray.dir, w.rect);
            if (t < best) {
                best = t;
                bestObject = null;
            }
        }
        for (GameObject o : level.objects()) {
            if (o == ray.from) {
                continue;
            }
            float t = distanceTo(o, ray);
            if (t < best) {
                best = t;
                bestObject = o;
            }
        }
        return new Hit(ray.origin.cpy().mulAdd(ray.dir, best), bestObject);
    }

    private static float distanceTo(GameObject o, Ray ray) {
        if (o instanceof Mirror) {
            Vector2[] e = ((Mirror) o).endpoints();
            return RayMath.segment(ray.origin, ray.dir, e[0], e[1]);
        }
        if (o instanceof Target) {
            return RayMath.circle(ray.origin, ray.dir, o.pos, o.radius);
        }
        return RayMath.MISS;
    }
}
