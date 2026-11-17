package com.prismpath.level;

import com.prismpath.light.LightColor;
import com.prismpath.light.LightSource;
import com.prismpath.light.Mirror;
import com.prismpath.light.Prism;
import com.prismpath.light.Target;
import com.prismpath.model.Level;
import com.prismpath.model.Player;
import com.prismpath.model.Wall;

/**
 * Vorläufiges Testlevel im Code, bis der LevelLoader Level aus
 * {@code levels/*.json} liest (NFA05).
 *
 * <p>Weißes Licht trifft ein Prisma. Blau erreicht sein Ziel direkt, Rot und
 * Grün brauchen Spiegel; Grün muss um eine Wand herumgelenkt werden.
 */
public final class DemoLevel {

    public static final float WIDTH = 1280;
    public static final float HEIGHT = 720;
    private static final float BORDER = 20;

    private DemoLevel() {
    }

    public static Level create() {
        Level level = new Level(WIDTH, HEIGHT);
        level.addWall(new Wall(0, 0, WIDTH, BORDER));
        level.addWall(new Wall(0, HEIGHT - BORDER, WIDTH, BORDER));
        level.addWall(new Wall(0, 0, BORDER, HEIGHT));
        level.addWall(new Wall(WIDTH - BORDER, 0, BORDER, HEIGHT));
        level.addWall(new Wall(760, 260, 20, 200));

        level.addObject(new LightSource(60, 360, 0, LightColor.WHITE, false, false));
        level.addObject(new Prism(320, 360, 0, true, true));

        level.addObject(new Mirror(480, 140, 0, true, true));
        level.addObject(new Mirror(560, 600, 90, true, true));
        level.addObject(new Mirror(1000, 230, 45, true, true));

        level.addObject(new Target(1100, 644, LightColor.BLUE));
        level.addObject(new Target(1200, 560, LightColor.GREEN));
        level.addObject(new Target(200, 100, LightColor.RED));

        level.addPlayer(new Player(1, 160, 220, 0));
        level.addPlayer(new Player(2, 160, 500, 0));
        return level;
    }
}
