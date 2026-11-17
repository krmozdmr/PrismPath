package com.prismpath.light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Die sieben Lichtfarben als Bitmaske aus Rot, Grün und Blau.
 *
 * <p>Mischen ist ein bitweises Oder (Frage 4): Rot + Grün = Gelb,
 * Grün + Blau = Cyan, Rot + Blau = Magenta, alle drei = Weiß.
 */
public enum LightColor {
    RED(0b001),
    GREEN(0b010),
    YELLOW(0b011),
    BLUE(0b100),
    MAGENTA(0b101),
    CYAN(0b110),
    WHITE(0b111);

    private static final LightColor[] BY_MASK = new LightColor[8];

    static {
        for (LightColor c : values()) {
            BY_MASK[c.mask] = c;
        }
    }

    public final int mask;

    LightColor(int mask) {
        this.mask = mask;
    }

    /**
     * Farbe zu einer Bitmaske.
     *
     * @throws IllegalArgumentException bei 0 (kein Licht) oder Werten außerhalb 1..7
     */
    public static LightColor fromMask(int mask) {
        if (mask < 1 || mask > 7) {
            throw new IllegalArgumentException("Ungültige Farbmaske: " + mask);
        }
        return BY_MASK[mask];
    }

    /** Additive Mischung beider Farben. */
    public LightColor mix(LightColor other) {
        return fromMask(mask | other.mask);
    }

    /** {@code true} für Rot, Grün und Blau. */
    public boolean isPrimary() {
        return Integer.bitCount(mask) == 1;
    }

    /** {@code true}, wenn alle Anteile von {@code other} in dieser Farbe enthalten sind. */
    public boolean contains(LightColor other) {
        return (mask & other.mask) == other.mask;
    }

    /** Die Grundfarben dieser Farbe in der Reihenfolge Rot, Grün, Blau. */
    public List<LightColor> components() {
        List<LightColor> result = new ArrayList<>(3);
        for (LightColor primary : new LightColor[] {RED, GREEN, BLUE}) {
            if (contains(primary)) {
                result.add(primary);
            }
        }
        return Collections.unmodifiableList(result);
    }
}
