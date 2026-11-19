package com.prismpath.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/** Kleine Helfer für die festen Layouts der Menüs (1280 × 720). */
final class Ui {

    private final Stage stage;
    private final Skin skin;

    Ui(Stage stage, Skin skin) {
        this.stage = stage;
        this.skin = skin;
    }

    Image plate(float x, float y, float w, float h, Color color) {
        Image image = new Image(skin.newDrawable("white", color));
        image.setBounds(x, y, w, h);
        image.setTouchable(Touchable.disabled);
        stage.addActor(image);
        return image;
    }

    Label label(String text, float x, float y, float w, float h, float scale, Color color, int align) {
        Label label = new Label(text, skin);
        label.setFontScale(scale);
        label.setColor(color);
        label.setAlignment(align);
        label.setBounds(x, y, w, h);
        label.setTouchable(Touchable.disabled);
        stage.addActor(label);
        return label;
    }

    TextButton button(String text, float x, float y, float w, float h, float scale) {
        TextButton button = new TextButton(text, skin);
        button.getLabel().setFontScale(scale);
        button.setBounds(x, y, w, h);
        stage.addActor(button);
        return button;
    }

    /**
     * Wechselt den Screen erst nach der laufenden Eingabe, damit der alte
     * Screen nicht mitten in einem Klick freigegeben wird.
     */
    static ChangeListener navigate(Runnable action) {
        return new ChangeListener() {
            private boolean done;

            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!done) {
                    done = true;
                    Gdx.app.postRunnable(action);
                }
            }
        };
    }
}
