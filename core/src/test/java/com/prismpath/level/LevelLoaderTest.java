package com.prismpath.level;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.math.Rectangle;
import com.prismpath.light.LightColor;
import com.prismpath.light.LightSource;
import com.prismpath.light.Mirror;
import com.prismpath.light.Mixer;
import com.prismpath.light.Prism;
import com.prismpath.light.Target;
import com.prismpath.model.Level;
import com.prismpath.model.Wall;
import org.junit.jupiter.api.Test;

class LevelLoaderTest {

    private static final String MINIMAL =
        "{ \"width\": 800, \"height\": 600, \"players\": [ {\"x\": 100, \"y\": 100} ] }";

    @Test
    void minimalesLevel() {
        Level level = LevelLoader.parse(MINIMAL);
        assertEquals(800, level.width);
        assertEquals(600, level.height);
        assertEquals("", level.name);
        assertEquals(1, level.players().size());
        assertTrue(level.walls().isEmpty());
    }

    @Test
    void allesWirdGelesen() {
        Level level = LevelLoader.parse("{"
            + "\"name\": \"Test\", \"width\": 800, \"height\": 600, \"border\": 20,"
            + "\"walls\": [ {\"x\": 300, \"y\": 0, \"w\": 20, \"h\": 200} ],"
            + "\"objects\": ["
            + "  {\"type\": \"source\", \"x\": 50, \"y\": 300, \"color\": \"white\"},"
            + "  {\"type\": \"mirror\", \"x\": 200, \"y\": 300, \"angle\": 45},"
            + "  {\"type\": \"prism\", \"x\": 250, \"y\": 300, \"movable\": false},"
            + "  {\"type\": \"mixer\", \"x\": 400, \"y\": 300, \"angle\": 90},"
            + "  {\"type\": \"target\", \"x\": 700, \"y\": 300, \"color\": \"YELLOW\"}"
            + "],"
            + "\"players\": [ {\"x\": 100, \"y\": 100, \"facing\": 90}, {\"x\": 100, \"y\": 500} ]"
            + "}");
        assertEquals("Test", level.name);
        assertEquals(5, level.walls().size());
        assertEquals(5, level.objects().size());

        LightSource s = assertInstanceOf(LightSource.class, level.objects().get(0));
        assertEquals(LightColor.WHITE, s.color);
        assertFalse(s.movable);
        Mirror m = assertInstanceOf(Mirror.class, level.objects().get(1));
        assertEquals(45, m.angle);
        assertTrue(m.movable && m.rotatable);
        Prism p = assertInstanceOf(Prism.class, level.objects().get(2));
        assertFalse(p.movable);
        assertTrue(p.rotatable);
        assertInstanceOf(Mixer.class, level.objects().get(3));
        assertEquals(LightColor.YELLOW, assertInstanceOf(Target.class, level.objects().get(4)).color);

        assertEquals(2, level.players().size());
        assertEquals(1, level.players().get(0).id);
        assertEquals(90, level.players().get(0).facing);
        assertEquals(2, level.players().get(1).id);
    }

    @Test
    void kaputtesJson() {
        assertThrows(LevelFormatException.class, () -> LevelLoader.parse("{ width: "));
    }

    @Test
    void fehlendeGroesse() {
        LevelFormatException e = assertThrows(LevelFormatException.class,
            () -> LevelLoader.parse("{ \"height\": 600 }"));
        assertTrue(e.getMessage().contains("width"));
    }

    @Test
    void unbekannterTypNenntDieStelle() {
        LevelFormatException e = assertThrows(LevelFormatException.class, () -> LevelLoader.parse(
            "{ \"width\": 800, \"height\": 600, \"objects\": [ {\"type\": \"lamp\", \"x\": 1, \"y\": 1} ],"
                + " \"players\": [ {\"x\": 1, \"y\": 1} ] }"));
        assertTrue(e.getMessage().contains("objects[0]"));
        assertTrue(e.getMessage().contains("lamp"));
    }

    @Test
    void unbekannteFarbe() {
        assertThrows(LevelFormatException.class, () -> LevelLoader.parse(
            "{ \"width\": 800, \"height\": 600, \"objects\": [ {\"type\": \"target\", \"x\": 1, \"y\": 1,"
                + " \"color\": \"PINK\"} ], \"players\": [ {\"x\": 1, \"y\": 1} ] }"));
    }

    @Test
    void ohneFigurenUngueltig() {
        assertThrows(LevelFormatException.class,
            () -> LevelLoader.parse("{ \"width\": 800, \"height\": 600 }"));
    }

    @Test
    void tuerLaesstEineLueckeImRahmen() {
        Level level = LevelLoader.parse("{ \"width\": 800, \"height\": 600, \"border\": 20,"
            + " \"door\": {\"side\": \"right\", \"at\": 300}, \"players\": [ {\"x\": 100, \"y\": 100} ] }");
        Rectangle door = level.door().rect;
        assertEquals(new Rectangle(780, 300 - LevelLoader.DOOR_WIDTH / 2, 20, LevelLoader.DOOR_WIDTH), door);
        assertEquals(5, level.walls().size(), "rechte Seite in zwei Stücken");
        for (Wall w : level.walls()) {
            assertFalse(w.rect.overlaps(door));
        }
    }

    @Test
    void tuerOhneRahmenUngueltig() {
        assertThrows(LevelFormatException.class, () -> LevelLoader.parse(
            "{ \"width\": 800, \"height\": 600, \"door\": {\"side\": \"top\", \"at\": 400},"
                + " \"players\": [ {\"x\": 1, \"y\": 1} ] }"));
    }
}
