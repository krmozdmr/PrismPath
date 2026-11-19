package com.prismpath.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prismpath.PrismPathGame;
import com.prismpath.ui.DraftSkin;

/** Hauptmenü als schlichter Entwurf: Spielen, Musik, Beenden. */
public final class MenuScreen extends ScreenAdapter {

    private final Stage stage = new Stage(new FitViewport(1280, 720));
    private final Skin skin = DraftSkin.create();

    public MenuScreen(PrismPathGame game) {
        Ui ui = new Ui(stage, skin);
        ui.plate(390, 150, 500, 440, DraftSkin.PANEL);
        ui.label("Prism Path", 390, 480, 500, 80, 4f, DraftSkin.TEXT, Align.center);
        ui.label("Kooperatives Lichträtsel für zwei Spieler", 390, 440, 500, 30, 1.2f,
            DraftSkin.MUTED, Align.center);

        TextButton play = ui.button("Spielen", 490, 340, 300, 64, 1.6f);
        play.addListener(Ui.navigate(game::showLevelSelect));

        TextButton music = ui.button(musicText(game), 490, 260, 300, 56, 1.3f);
        music.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.music().toggle();
                music.setText(musicText(game));
            }
        });

        TextButton quit = ui.button("Beenden", 490, 180, 300, 56, 1.3f);
        quit.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        ui.label("Entwurf - Grafik folgt", 0, 30, 1280, 24, 1f, DraftSkin.MUTED, Align.center);

        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                    return true;
                }
                return false;
            }
        });
    }

    private static String musicText(PrismPathGame game) {
        return game.music().isEnabled() ? "Musik: an" : "Musik: aus";
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(DraftSkin.BACKGROUND);
        stage.getViewport().apply();
        stage.act(Math.min(delta, 1f / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        if (Gdx.input.getInputProcessor() == stage) {
            Gdx.input.setInputProcessor(null);
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
