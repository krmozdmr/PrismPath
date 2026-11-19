package com.prismpath.level;

import com.badlogic.gdx.math.Rectangle;
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
 * Spiegel, Prismen und Mixer sind beweglich und drehbar. Eine Tür ist eine
 * Lücke im Rahmen ({@code border}).
 */
public final class LevelLoader {

    /** Breite der Türöffnung; zwei Figuren passen nebeneinander hinein. */
    public static final float DOOR_WIDTH = 120f;

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
        Rectangle door = door(root, width, height, border);
        if (border > 0) {
            frame(level, new Rectangle(0, 0, width, border), door);
            frame(level, new Rectangle(0, height - border, width, border), door);
            frame(level, new Rectangle(0, 0, border, height), door);
            frame(level, new Rectangle(width - border, 0, border, height), door);
        }
        if (door != null) {
            level.setDoor(new Wall(door.x, door.y, door.width, door.height));
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

    /**
     * Türöffnung im Rahmen oder {@code null}. {@code side} ist die Seite,
     * {@code at} die Mitte der Öffnung entlang dieser Seite.
     */
    private static Rectangle door(JsonValue root, float width, float height, float border) {
        JsonValue d = root.get("door");
        if (d == null) {
            return null;
        }
        if (border <= 0) {
            throw new LevelFormatException("door: eine Tür braucht einen Rahmen (\"border\")");
        }
        String side = text(d, "side", "door");
        float start = number(d, "at", "door") - DOOR_WIDTH / 2;
        Rectangle r;
        switch (side) {
            case "left":
                r = new Rectangle(0, start, border, DOOR_WIDTH);
                break;
            case "right":
                r = new Rectangle(width - border, start, border, DOOR_WIDTH);
                break;
            case "bottom":
                r = new Rectangle(start, 0, DOOR_WIDTH, border);
                break;
            case "top":
                r = new Rectangle(start, height - border, DOOR_WIDTH, border);
                break;
            default:
                throw new LevelFormatException("door: unbekannte Seite \"" + side + "\"");
        }
        float length = side.equals("left") || side.equals("right") ? height : width;
        if (start < border || start + DOOR_WIDTH > length - border) {
            throw new LevelFormatException("door: Öffnung reicht in eine Ecke");
        }
        return r;
    }

    /** Legt eine Seite des Rahmens an und lässt dabei die Türöffnung frei. */
    private static void frame(Level level, Rectangle side, Rectangle door) {
        if (door == null || !side.overlaps(door)) {
            level.addWall(new Wall(side.x, side.y, side.width, side.height));
        } else if (side.width > side.height) {
            level.addWall(new Wall(side.x, side.y, door.x - side.x, side.height));
            level.addWall(new Wall(door.x + door.width, side.y,
                side.x + side.width - door.x - door.width, side.height));
        } else {
            level.addWall(new Wall(side.x, side.y, side.width, door.y - side.y));
            level.addWall(new Wall(side.x, door.y + door.height,
                side.width, side.y + side.height - door.y - door.height));
        }
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
