package com.prismpath.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.prismpath.light.BeamSegment;
import com.prismpath.light.LightSource;
import com.prismpath.light.Mirror;
import com.prismpath.light.Mixer;
import com.prismpath.light.Prism;
import com.prismpath.light.Target;
import com.prismpath.light.TraceResult;
import com.prismpath.model.GameObject;
import com.prismpath.model.Level;
import com.prismpath.model.Player;
import com.prismpath.model.Wall;

/**
 * Zeichnet Level, Strahlen und Figuren mit einfachen Formen.
 * Ändert den Spielzustand nie.
 */
public final class LevelRenderer {

    private static final Color FLOOR = Color.valueOf("141a24");
    private static final Color WALL = Color.valueOf("3a4558");
    private static final Color DOOR = Color.valueOf("c8a060");
    private static final Color GLASS = Color.valueOf("9fd8ff");
    private static final Color METAL = Color.valueOf("c9d3e0");
    private static final Color PLAYER_1 = Color.valueOf("4f8cff");
    private static final Color PLAYER_2 = Color.valueOf("49d17d");
    private static final float BEAM_WIDTH = 3f;
    /** So stark wird der Fokusring beim Blinken höchstens abgedunkelt. */
    private static final float FOCUS_DIM = 0.6f;

    private final ShapeRenderer shapes;
    private final Color tmp = new Color();

    public LevelRenderer(ShapeRenderer shapes) {
        this.shapes = shapes;
    }

    public void renderWorld(Level level, TraceResult trace) {
        shapes.begin(ShapeType.Filled);
        shapes.setColor(FLOOR);
        shapes.rect(0, 0, level.width, level.height);
        for (Wall w : level.walls()) {
            shapes.setColor(WALL);
            shapes.rect(w.rect.x, w.rect.y, w.rect.width, w.rect.height);
        }
        if (level.door() != null && !level.isDoorOpen()) {
            drawDoor(level.door());
        }
        for (BeamSegment b : trace.beams) {
            shapes.setColor(LightPalette.of(b.color));
            shapes.rectLine(b.start, b.end, BEAM_WIDTH);
        }
        for (GameObject o : level.objects()) {
            drawObject(o);
        }
        for (Player p : level.players()) {
            drawPlayer(p);
        }
        shapes.end();

        shapes.begin(ShapeType.Line);
        if (level.isDoorOpen()) {
            drawDoor(level.door());
        }
        for (Player p : level.players()) {
            drawFocus(level, p);
        }
        shapes.end();
    }

    /** Geschlossen als volles Rechteck (Filled), offen nur als Rahmen (Line). */
    private void drawDoor(Wall door) {
        shapes.setColor(DOOR);
        shapes.rect(door.rect.x, door.rect.y, door.rect.width, door.rect.height);
    }

    private void drawObject(GameObject o) {
        if (o instanceof Mirror) {
            Vector2[] e = ((Mirror) o).endpoints();
            shapes.setColor(METAL);
            shapes.rectLine(e[0], e[1], 6f);
        } else if (o instanceof Prism) {
            drawPrism((Prism) o);
        } else if (o instanceof Mixer) {
            shapes.setColor(METAL);
            shapes.circle(o.pos.x, o.pos.y, o.radius);
            shapes.setColor(FLOOR);
            shapes.circle(o.pos.x, o.pos.y, o.radius - 5f);
            pointer(o.pos, o.angle, o.radius + 6f, METAL);
        } else if (o instanceof Target) {
            Target t = (Target) o;
            Color c = LightPalette.of(t.color);
            shapes.setColor(c);
            shapes.circle(t.pos.x, t.pos.y, t.radius);
            if (!t.lit) {
                shapes.setColor(tmp.set(FLOOR).lerp(c, 0.15f));
                shapes.circle(t.pos.x, t.pos.y, t.radius - 4f);
            }
        } else if (o instanceof LightSource) {
            LightSource s = (LightSource) o;
            shapes.setColor(WALL);
            shapes.circle(s.pos.x, s.pos.y, s.radius);
            shapes.setColor(LightPalette.of(s.color));
            shapes.circle(s.pos.x, s.pos.y, s.radius * 0.5f);
            pointer(s.pos, s.angle, s.radius + 4f, LightPalette.of(s.color));
        }
    }

    private void drawPrism(Prism p) {
        Vector2[] v = p.vertices();
        shapes.setColor(GLASS);
        shapes.triangle(v[0].x, v[0].y, v[1].x, v[1].y, v[2].x, v[2].y);
    }

    private void drawPlayer(Player p) {
        Color c = colorOf(p);
        shapes.setColor(c);
        shapes.circle(p.pos.x, p.pos.y, Player.RADIUS);
        pointer(p.pos, p.facing, Player.RADIUS + 5f, Color.WHITE);
        if (p.held != null) {
            shapes.setColor(tmp.set(c).mul(1, 1, 1, 0.6f));
            shapes.rectLine(p.pos, p.held.pos, 2f);
        }
    }

    /**
     * Dünner Ring in der Farbe der Figur um das Objekt, mit dem sie gerade etwas
     * tun kann: durchgehend beim getragenen Objekt, leicht blinkend in Reichweite.
     */
    private void drawFocus(Level level, Player p) {
        GameObject o = p.focus(level);
        if (o == null) {
            return;
        }
        tmp.set(colorOf(p));
        if (o != p.held) {
            float phase = TimeUtils.millis() % 1000 / 1000f; // eine Sekunde pro Blinken
            tmp.lerp(FLOOR, FOCUS_DIM * (0.5f + 0.5f * MathUtils.sinDeg(phase * 360f)));
        }
        shapes.setColor(tmp);
        // Etwas Abstand je Figur, damit sich zwei Ringe um dasselbe Objekt nicht überdecken.
        shapes.circle(o.pos.x, o.pos.y, o.radius + 3f + 3f * p.id);
    }

    private static Color colorOf(Player p) {
        return p.id == 1 ? PLAYER_1 : PLAYER_2;
    }

    private void pointer(Vector2 from, float angle, float length, Color color) {
        shapes.setColor(color);
        shapes.rectLine(from.x, from.y,
            from.x + length * MathUtils.cosDeg(angle),
            from.y + length * MathUtils.sinDeg(angle), 3f);
    }
}
