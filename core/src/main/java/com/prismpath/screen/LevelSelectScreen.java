package com.prismpath.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prismpath.PrismPathGame;
import com.prismpath.level.LevelCatalog;
import com.prismpath.ui.DraftSkin;

/**
 * Levelauswahl als Entwurf: eine Karte pro Level aus {@code levels/index.json}
 * mit Nummer, Name und Vorschau des Ausgangszustands. Bis zu vier pro Reihe.
 */
public final class LevelSelectScreen extends ScreenAdapter {

    private static final int PER_ROW = 4;
    private static final float CARD_W = 248;
    private static final float GAP = 24;

    private final Stage stage = new Stage(new FitViewport(1280, 720));
    private final Skin skin = DraftSkin.create();

    public LevelSelectScreen(PrismPathGame game) {
        Ui ui = new Ui(stage, skin);
        ui.label("Level wählen", 0, 600, 1280, 60, 2.6f, DraftSkin.TEXT, Align.center);

        LevelCatalog catalog = game.catalog();
        Texture white = skin.get("white", Texture.class);
        int rows = (catalog.size() + PER_ROW - 1) / PER_ROW;
        float cardH = rows > 1 ? 200 : 260;
        float top = 560;
        for (int i = 0; i < catalog.size(); i++) {
            int row = i / PER_ROW;
            int inRow = Math.min(PER_ROW, catalog.size() - row * PER_ROW);
            float x = (1280 - inRow * CARD_W - (inRow - 1) * GAP) / 2f + (i % PER_ROW) * (CARD_W + GAP);
            float y = top - (row + 1) * cardH - row * 16;

            Button card = new Button(skin.get(TextButton.TextButtonStyle.class));
            card.setBounds(x, y, CARD_W, cardH);
            card.pad(14);
            final int index = i;
            card.addListener(Ui.navigate(() -> game.startLevel(index)));

            Label title = new Label((i + 1) + "  " + catalog.name(i), skin);
            title.setFontScale(1.3f);
            title.setEllipsis(true);
            title.setTouchable(Touchable.disabled);
            card.add(title).growX().left().row();
            card.add(new LevelPreview(catalog.create(i), white))
                .size(CARD_W - 28, cardH - 60).padTop(10);
            stage.addActor(card);
        }

        TextButton back = ui.button("Zurück", 108, 60, 200, 56, 1.3f);
        back.addListener(Ui.navigate(game::showMenu));
        ui.label("Esc: Hauptmenü", 0, 24, 1280, 24, 1f, DraftSkin.MUTED, Align.center);

        stage.addListener(new InputListener() {
            private boolean done;

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE && !done) {
                    done = true;
                    Gdx.app.postRunnable(game::showMenu);
                    return true;
                }
                return false;
            }
        });
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
