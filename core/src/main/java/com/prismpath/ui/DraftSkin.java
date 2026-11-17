package com.prismpath.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Schlichtes Aussehen der Menüs als Entwurf: Standardschrift von libGDX und
 * einfarbige Knöpfe. Wird ersetzt, sobald das Pixel-Art-Design feststeht.
 */
public final class DraftSkin {

    public static final Color BACKGROUND = Color.valueOf("141a24");
    public static final Color PANEL = Color.valueOf("1d2633");
    public static final Color TEXT = Color.valueOf("eef2f7");
    public static final Color MUTED = Color.valueOf("8d9bb0");

    private DraftSkin() {
    }

    public static Skin create() {
        Skin skin = new Skin();
        Pixmap pixel = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixel.setColor(Color.WHITE);
        pixel.fill();
        skin.add("white", new Texture(pixel));
        pixel.dispose();

        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);
        skin.add("default", new Label.LabelStyle(font, TEXT));

        TextButton.TextButtonStyle buttons = new TextButton.TextButtonStyle();
        buttons.font = font;
        buttons.fontColor = TEXT;
        buttons.up = skin.newDrawable("white", Color.valueOf("2c3a4f"));
        buttons.over = skin.newDrawable("white", Color.valueOf("3d5170"));
        buttons.down = skin.newDrawable("white", Color.valueOf("23527a"));
        buttons.focused = buttons.over;
        skin.add("default", buttons);
        return skin;
    }
}
