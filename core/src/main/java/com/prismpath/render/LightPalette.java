package com.prismpath.render;

import com.badlogic.gdx.graphics.Color;
import com.prismpath.light.LightColor;
import java.util.EnumMap;
import java.util.Map;

/** Bildschirmfarbe jeder Lichtfarbe. */
public final class LightPalette {

    private static final Map<LightColor, Color> COLORS = new EnumMap<>(LightColor.class);

    static {
        COLORS.put(LightColor.RED, Color.valueOf("ff3b4e"));
        COLORS.put(LightColor.GREEN, Color.valueOf("3ddc6a"));
        COLORS.put(LightColor.BLUE, Color.valueOf("3b82ff"));
        COLORS.put(LightColor.YELLOW, Color.valueOf("ffd93b"));
        COLORS.put(LightColor.CYAN, Color.valueOf("38e1ff"));
        COLORS.put(LightColor.MAGENTA, Color.valueOf("e04bff"));
        COLORS.put(LightColor.WHITE, Color.valueOf("f4f6fb"));
    }

    private LightPalette() {
    }

    /** Die Farbe darf nicht verändert werden; bei Bedarf {@code cpy()} verwenden. */
    public static Color of(LightColor c) {
        return COLORS.get(c);
    }
}
