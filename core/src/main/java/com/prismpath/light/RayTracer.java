package com.prismpath.light;

import com.badlogic.gdx.math.Vector2;
import com.prismpath.model.GameObject;
import com.prismpath.model.Level;
import com.prismpath.model.Wall;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Verfolgt alle Lichtstrahlen durch das Level, setzt {@link Target#lit} und
 * liefert die Strahlstücke als {@link TraceResult}.
 *
 * <p>Lichtquellen blockieren kein Licht. Jedes gerade Strahlstück ist ein Strahl.
 * Feste Grenzen verhindern Endlosschleifen (Frage 6): {@value #MAX_BOUNCES}
 * Umlenkungen pro Strahl, {@value #MAX_RAYS} Strahlen insgesamt und
 * {@value #MAX_MIXER_PASSES} Durchläufe für Mixer.
 *
 * <p>Mixer senden aus, was bei ihnen ankommt; was ankommt, hängt aber auch
 * von den Mixern ab. Deshalb rechnet der RayTracer so lange neu, bis sich die
 * Ausgänge aller Mixer nicht mehr ändern.
 */
public final class RayTracer {

    public static final int MAX_BOUNCES = 64;
    public static final int MAX_RAYS = 512;
    public static final int MAX_MIXER_PASSES = 10;

    private final int maxBounces;
    private final int maxRays;
    private final int maxMixerPasses;

    public RayTracer() {
        this(MAX_BOUNCES, MAX_RAYS, MAX_MIXER_PASSES);
    }

    /** Mit eigenen Grenzen, nur für Tests. */
    RayTracer(int maxBounces, int maxRays, int maxMixerPasses) {
        if (maxBounces < 0 || maxRays < 1 || maxMixerPasses < 1) {
            throw new IllegalArgumentException("Ungültige Grenzen");
        }
        this.maxBounces = maxBounces;
        this.maxRays = maxRays;
        this.maxMixerPasses = maxMixerPasses;
    }

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
        /** Bitmaske der Farben, die bei jedem Mixer ankommen. */
        final Map<Mixer, Integer> mixerInput = new HashMap<>();
        int rays;
        boolean truncated;
    }

    public TraceResult trace(Level level) {
        Map<Mixer, LightColor> outputs = new HashMap<>();
        Pass pass = null;
        boolean stable = false;
        for (int i = 0; i < maxMixerPasses && !stable; i++) {
            pass = runPass(level, outputs);
            Map<Mixer, LightColor> next = new HashMap<>();
            for (Map.Entry<Mixer, Integer> e : pass.mixerInput.entrySet()) {
                LightColor out = e.getKey().output(e.getValue());
                if (out != null) {
                    next.put(e.getKey(), out);
                }
            }
            stable = next.equals(outputs);
            outputs = next;
        }
        for (GameObject o : level.objects()) {
            if (o instanceof Target) {
                ((Target) o).lit = pass.lit.contains(o);
            }
        }
        return new TraceResult(pass.beams, pass.truncated, !stable);
    }

    private Pass runPass(Level level, Map<Mixer, LightColor> mixerOutputs) {
        Pass pass = new Pass();
        for (GameObject o : level.objects()) {
            if (o instanceof LightSource) {
                LightSource s = (LightSource) o;
                Vector2 dir = s.direction();
                Vector2 origin = s.pos.cpy().mulAdd(dir, s.radius);
                emit(pass, origin, dir, s.color, 0, s);
            }
        }
        for (Map.Entry<Mixer, LightColor> e : mixerOutputs.entrySet()) {
            Mixer m = e.getKey();
            emit(pass, m.pos.cpy(), m.direction(), e.getValue(), 0, m);
        }
        while (!pass.queue.isEmpty()) {
            follow(level, pass, pass.queue.poll());
        }
        return pass;
    }

    private void emit(Pass pass, Vector2 origin, Vector2 dir, LightColor color, int bounces, GameObject from) {
        if (pass.rays >= maxRays) {
            pass.truncated = true;
            return;
        }
        pass.rays++;
        pass.queue.add(new Ray(origin, dir, color, bounces, from));
    }

    private void follow(Level level, Pass pass, Ray ray) {
        Hit hit = nearestHit(level, ray);
        GameObject o = hit.object;
        // Prisma und Mixer zählen jeden Treffer, als ginge er durch die Mitte. Der
        // Strahl knickt aber nicht zur Mitte ab, sondern läuft auf seiner Linie weiter.
        Vector2 end = o instanceof Prism || o instanceof Mixer ? closestToCenter(ray, o) : hit.point;
        pass.beams.add(new BeamSegment(ray.origin, end, ray.color));
        if (o instanceof Prism) {
            if (!canBounce(pass, ray)) {
                return;
            }
            split(pass, (Prism) o, ray, end);
        } else if (o instanceof Mixer) {
            pass.mixerInput.merge((Mixer) o, ray.color.mask, (a, b) -> a | b);
        } else if (o instanceof Mirror) {
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

    /**
     * Grundfarben gehen gerade durch, Mischfarben werden zerlegt (Frage 3).
     * Die neuen Strahlen starten bei {@code at}, die Winkel sind dieselben wie
     * bei einem Treffer in der Mitte.
     */
    private void split(Pass pass, Prism prism, Ray ray, Vector2 at) {
        if (ray.color.isPrimary()) {
            emit(pass, at.cpy(), ray.dir.cpy(), ray.color, ray.bounces + 1, prism);
            return;
        }
        float base = ray.dir.angleDeg() + prism.steer();
        for (LightColor c : ray.color.components()) {
            Vector2 dir = new Vector2(1, 0).setAngleDeg(base + Prism.offsetFor(c));
            emit(pass, at.cpy(), dir, c, ray.bounces + 1, prism);
        }
    }

    /** Punkt auf dem Strahl, der der Mitte des Objekts am nächsten liegt. */
    private static Vector2 closestToCenter(Ray ray, GameObject o) {
        float t = Math.max(0, new Vector2(o.pos).sub(ray.origin).dot(ray.dir));
        return ray.origin.cpy().mulAdd(ray.dir, t);
    }

    private boolean canBounce(Pass pass, Ray ray) {
        if (ray.bounces >= maxBounces) {
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
        if (o instanceof Target || o instanceof Prism || o instanceof Mixer) {
            return RayMath.circle(ray.origin, ray.dir, o.pos, o.radius);
        }
        return RayMath.MISS;
    }
}
