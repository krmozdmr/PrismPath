package com.prismpath.screen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.prismpath.light.LightSource;
import com.prismpath.light.Mirror;
import com.prismpath.light.Mixer;
import com.prismpath.light.Prism;
import com.prismpath.light.Target;
import com.prismpath.model.GameObject;
import com.prismpath.model.Level;
import com.prismpath.model.Player;
import com.prismpath.model.Wall;
import com.prismpath.render.LightPalette;

/** Verkleinerte Ansicht des Ausgangszustands eines Levels für die Levelauswahl. */
final class LevelPreview extends Actor {

    private static final Color FLOOR = Color.valueOf("0f1822");
    private static final Color WALL = Color.valueOf("4a5a70");
    private static final Color GLASS = Color.valueOf("9fd8ff");
    private static final Color METAL = Color.valueOf("c9d3e0");

    private final Level level;
    private final TextureRegion white;

    LevelPreview(Level level, Texture white) {
        this.level = level;
        this.white = new TextureRegion(white);
        setTouchable(Touchable.disabled);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float s = Math.min(getWidth() / level.width, getHeight() / level.height);
        float ox = getX() + (getWidth() - level.width * s) / 2;
        float oy = getY() + (getHeight() - level.height * s) / 2;
        rect(batch, FLOOR, ox, oy, level.width * s, level.height * s, 0);
        for (Wall w : level.walls()) {
            rect(batch, WALL, ox + w.rect.x * s, oy + w.rect.y * s, w.rect.width * s, w.rect.height * s, 0);
        }
        for (GameObject o : level.objects()) {
            float x = ox + o.pos.x * s;
            float y = oy + o.pos.y * s;
            if (o instanceof Mirror) {
                float len = Mirror.HALF_LENGTH * 2 * s;
                rect(batch, METAL, x - len / 2, y - 1.5f, len, 3, o.angle);
            } else {
                Color c = o instanceof Target ? LightPalette.of(((Target) o).color)
                    : o instanceof LightSource ? LightPalette.of(((LightSource) o).color)
                    : o instanceof Prism ? GLASS
                    : o instanceof Mixer ? METAL : Color.GRAY;
                float d = Math.max(5, o.radius * 2 * s);
                rect(batch, c, x - d / 2, y - d / 2, d, d, o instanceof Prism ? 45 : 0);
            }
        }
        for (Player p : level.players()) {
            float d = Math.max(5, Player.RADIUS * 2 * s);
            rect(batch, p.id == 1 ? Color.valueOf("4f8cff") : Color.valueOf("49d17d"),
                ox + p.pos.x * s - d / 2, oy + p.pos.y * s - d / 2, d, d, 0);
        }
        batch.setColor(Color.WHITE);
    }

    private void rect(Batch batch, Color c, float x, float y, float w, float h, float rotation) {
        batch.setColor(c.r, c.g, c.b, c.a * getColor().a);
        batch.draw(white, x, y, w / 2, h / 2, w, h, 1, 1, rotation);
    }
}
