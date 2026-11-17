package com.prismpath.level;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;
import com.prismpath.light.LightColor;
import com.prismpath.light.LightSource;
import com.prismpath.light.Mirror;
import com.prismpath.light.Mixer;
import com.prismpath.light.Prism;
import com.prismpath.light.Target;
import com.prismpath.model.GameObject;
import com.prismpath.model.Level;
import com.prismpath.model.Player;
import com.prismpath.model.Wall;
import java.util.Locale;

/**
 * Erzeugt ein {@link Level} aus dem JSON-Format der Dateien in {@code levels/}.
 * Das Format ist in der README beschrieben.
 *
 * <p>Fehlende Angaben haben Standardwerte: Winkel 0; Lichtquellen sind fest,
 * Spiegel, Prismen und Mixer sind beweglich und drehbar.
 */
public final class LevelLoader {

    private LevelLoader() {
    }

    public static Level parse(String json) {
        JsonValue root;
        try {
            root = new JsonReader().parse(json);
        } catch (SerializationException e) {
            throw new LevelFormatException("Kein gültiges JSON: " + e.getMessage(), e);
        }
        if (root == null || !root.isObject()) {
            throw new LevelFormatException("Ein Level muss ein JSON-Objekt sein");
        }
        float width = number(root, "width", "Level");
        float height = number(root, "height", "Level");
        Level level = new Level(root.getString("name", ""), width, height);

        float border = root.getFloat("border", 0);
        if (border > 0) {
            level.addWall(new Wall(0, 0, width, border));
            level.addWall(new Wall(0, height - border, width, border));
            level.addWall(new Wall(0, 0, border, height));
            level.addWall(new Wall(width - border, 0, border, height));
        }
        int i = 0;
        for (JsonValue w : list(root, "walls")) {
            String where = "walls[" + i++ + "]";
            level.addWall(new Wall(number(w, "x", where), number(w, "y", where),
                number(w, "w", where), number(w, "h", where)));
        }
        i = 0;
        for (JsonValue o : list(root, "objects")) {
            level.addObject(object(o, "objects[" + i++ + "]"));
        }
        i = 0;
        for (JsonValue p : list(root, "players")) {
            String where = "players[" + i + "]";
            level.addPlayer(new Player(++i, number(p, "x", where), number(p, "y", where),
                p.getFloat("facing", 0)));
        }
        if (level.players().isEmpty() || level.players().size() > 2) {
            throw new LevelFormatException("Ein Level braucht 1 oder 2 Figuren unter \"players\"");
        }
        return level;
    }

    private static GameObject object(JsonValue o, String where) {
        String type = text(o, "type", where);
        float x = number(o, "x", where);
        float y = number(o, "y", where);
        float angle = o.getFloat("angle", 0);
        switch (type) {
            case "source":
                return new LightSource(x, y, angle, color(o, where),
                    o.getBoolean("movable", false), o.getBoolean("rotatable", false));
            case "mirror":
                return new Mirror(x, y, angle,
                    o.getBoolean("movable", true), o.getBoolean("rotatable", true));
            case "prism":
                return new Prism(x, y, angle,
                    o.getBoolean("movable", true), o.getBoolean("rotatable", true));
            case "mixer":
                return new Mixer(x, y, angle, Mixer.Type.ADDITIVE,
                    o.getBoolean("movable", true), o.getBoolean("rotatable", true));
            case "target":
                return new Target(x, y, color(o, where));
            default:
                throw new LevelFormatException(where + ": unbekannter Typ \"" + type + "\"");
        }
    }

    private static LightColor color(JsonValue o, String where) {
        String name = text(o, "color", where);
        try {
            return LightColor.valueOf(name.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new LevelFormatException(where + ": unbekannte Farbe \"" + name + "\"");
        }
    }

    private static Iterable<JsonValue> list(JsonValue root, String key) {
        JsonValue v = root.get(key);
        if (v == null) {
            return new JsonValue(JsonValue.ValueType.array);
        }
        if (!v.isArray()) {
            throw new LevelFormatException("\"" + key + "\" muss eine Liste sein");
        }
        return v;
    }

    private static float number(JsonValue v, String key, String where) {
        JsonValue n = v.get(key);
        if (n == null || !n.isNumber()) {
            throw new LevelFormatException(where + ": Zahl \"" + key + "\" fehlt");
        }
        return n.asFloat();
    }

    private static String text(JsonValue v, String key, String where) {
        JsonValue s = v.get(key);
        if (s == null || !s.isString()) {
            throw new LevelFormatException(where + ": Text \"" + key + "\" fehlt");
        }
        return s.asString();
    }
}
