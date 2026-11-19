package com.prismpath.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.prismpath.PrismPathGame;
import com.prismpath.input.GdxKeyInput;
import com.prismpath.input.KeyInput;
import com.prismpath.input.PlayerController;
import com.prismpath.level.LevelCatalog;
import com.prismpath.light.RayTracer;
import com.prismpath.light.TraceResult;
import com.prismpath.model.Level;
import com.prismpath.render.LevelRenderer;

/**
 * Spielschleife eines Levels (Abb. 2): Eingabe, Strahlen, Levelende, Zeichnen.
 * Nach dem Lösen öffnet sich die Tür; stehen beide Figuren darin, beginnt das
 * nächste Level. R startet das Level neu, Esc öffnet das Menü (Frage 8).
 */
public final class GameScreen extends ScreenAdapter {

    /** Größter Zeitschritt, damit nach Rucklern niemand durch Wände springt. */
    private static final float MAX_DT = 1f / 20f;

    private final PrismPathGame game;
    private final LevelCatalog catalog;
    private final KeyInput input = new GdxKeyInput();
    private final PlayerController controller = new PlayerController();
    private final RayTracer tracer = new RayTracer();

    private final OrthographicCamera camera = new OrthographicCamera();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final LevelRenderer renderer = new LevelRenderer(shapes);
    private final FitViewport viewport;

    private int levelIndex;
    private Level level;
    private TraceResult trace = TraceResult.EMPTY;
    private boolean solved;

    public GameScreen(PrismPathGame game, int levelIndex) {
        this.game = game;
        this.catalog = game.catalog();
        this.viewport = new FitViewport(1280, 720, camera);
        load(levelIndex);
    }

    private void load(int index) {
        levelIndex = index;
        level = catalog.create(index);
        solved = false;
        viewport.setWorldSize(level.width, level.height);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    private boolean hasNextLevel() {
        return levelIndex + 1 < catalog.size();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.postRunnable(game::showMenu);
        }
        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            load(levelIndex);
        }
        if (level.isFinished() && hasNextLevel()) {
            load(levelIndex + 1);
        }
        float dt = Math.min(delta, MAX_DT);

        controller.update(level, input, dt);
        trace = tracer.trace(level);
        solved = level.isSolved();

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        shapes.setProjectionMatrix(camera.combined);
        renderer.renderWorld(level, trace);
        drawHud();
    }

    private void drawHud() {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "Level " + (levelIndex + 1) + "/" + catalog.size() + ": " + level.name,
            30, level.height - 30);
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "P1: WASD  Leertaste  Q/E (+Shift)     P2: Pfeile  M  Ö/Ç (+Shift)     R: neu   Esc: Menü",
            30, 45);
        if (solved) {
            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            font.draw(batch, "Level gelöst!", level.width / 2f - 90, level.height - 50);
            font.getData().setScale(1f);
            String next = !hasNextLevel() && level.isFinished()
                ? "Alle Level geschafft!" : "Beide zur Tür!";
            font.draw(batch, next, level.width / 2f - 70, level.height - 90);
        }
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapes.dispose();
        batch.dispose();
        font.dispose();
    }
}
