package com.prismpath.light;

import com.prismpath.model.GameObject;
import java.util.Objects;

/**
 * Vereint alle ankommenden Farben zu einem Strahl in Blickrichtung (Frage 4).
 * Für Strahlen und Kollision ist der Mixer ein Kreis.
 */
public final class Mixer extends GameObject {

    /**
     * Art der Mischung. Der Architekturentwurf sieht das Attribut vor,
     * beschreibt aber nur die additive Mischung.
     */
    public enum Type {
        /** Addiert alle ankommenden Farben (bitweises Oder). */
        ADDITIVE
    }

    public static final float RADIUS = 18f;

    public final Type type;

    public Mixer(float x, float y, float angle, Type type, boolean movable, boolean rotatable) {
        super(x, y, angle, RADIUS, movable, rotatable);
        this.type = Objects.requireNonNull(type, "type");
    }

    /**
     * Ausgangsfarbe für die Bitmaske aller ankommenden Farben.
     *
     * @return {@code null}, wenn kein Licht ankommt
     */
    public LightColor output(int incomingMask) {
        return incomingMask == 0 ? null : LightColor.fromMask(incomingMask);
    }
}
