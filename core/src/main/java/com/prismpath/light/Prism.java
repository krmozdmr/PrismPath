package com.prismpath.light;

import com.badlogic.gdx.math.MathUtils;
import com.prismpath.model.GameObject;

/**
 * Teilt Licht in Rot, Grün und Blau (Frage 3).
 *
 * <p>Grundfarben gehen gerade durch. Mischfarben werden zerlegt: jede
 * enthaltene Grundfarbe verlässt das Prisma mit {@link #offsetFor} gegenüber
 * der Grundrichtung. Die Grundrichtung ist die Einfallsrichtung, gedreht um
 * {@link #steer()}; so lenkt das Drehen des Prismas die Strahlen wie im
 * Prototyp. Das Prisma ist für Strahlen und Kollision ein Kreis.
 */
public final class Prism extends GameObject {

    public static final float RADIUS = 18f;
    /** Größte Ablenkung durch das Drehen des Prismas in Grad. */
    public static final float MAX_STEER = 45f;

    public Prism(float x, float y, float angle, boolean movable, boolean rotatable) {
        super(x, y, angle, RADIUS, movable, rotatable);
    }

    /**
     * Ablenkung einer Grundfarbe beim Zerlegen: Rot −20°, Grün 0°, Blau +20°.
     *
     * @throws IllegalArgumentException für Mischfarben
     */
    public static float offsetFor(LightColor color) {
        switch (color) {
            case RED:
                return -20f;
            case GREEN:
                return 0f;
            case BLUE:
                return 20f;
            default:
                throw new IllegalArgumentException("Keine Grundfarbe: " + color);
        }
    }

    /**
     * Drehung der Grundrichtung durch den Prismenwinkel. Ein dreiseitiges
     * Prisma wiederholt sich alle 120°, daher {@code sin(3 · Winkel)}.
     */
    public float steer() {
        return MAX_STEER * MathUtils.sinDeg(3f * angle);
    }
}
