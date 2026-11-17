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
import com.prismpath.input.GdxKeyInput;
import com.prismpath.input.KeyInput;
import com.prismpath.input.PlayerController;
import com.prismpath.light.RayTracer;
import com.prismpath.light.TraceResult;
import com.prismpath.model.Level;
import com.prismpath.render.LevelRenderer;
import java.util.function.Supplier;

/**
 * Spielschleife eines Levels (Abb. 2): Eingabe, Strahlen, Levelende, Zeichnen.
 * R startet das Level neu, Esc beendet vorerst das Spiel (das Menü folgt).
 */
public final class GameScreen extends ScreenAdapter {

    /** Größter Zeitschritt, damit nach Rucklern niemand durch Wände springt. */
    private static final float MAX_DT = 1f / 20f;

    private final Supplier<Level> levelFactory;
    private final KeyInput input = new GdxKeyInput();
    private final PlayerController controller = new PlayerController();
    private final RayTracer tracer = new RayTracer();

    private final OrthographicCamera camera = new OrthographicCamera();
    private final ShapeRenderer shapes = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final BitmapFont font = new BitmapFont();
    private final LevelRenderer renderer = new LevelRenderer(shapes);
    private final FitViewport viewport;

    private Level level;
    private TraceResult trace = TraceResult.EMPTY;
    private boolean solved;

    public GameScreen(Supplier<Level> levelFactory) {
        this.levelFactory = levelFactory;
        this.level = levelFactory.get();
        this.viewport = new FitViewport(level.width, level.height, camera);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            Gdx.app.exit();
            return;
        }
        if (Gdx.input.isKeyJustPressed(Keys.R)) {
            level = levelFactory.get();
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
        font.setColor(Color.LIGHT_GRAY);
        font.draw(batch, "P1: WASD  Leertaste  Q/E (+Shift)     P2: Pfeile  M  Ö/Ç (+Shift)     R: neu   Esc: Ende",
            30, 45);
        if (solved) {
            font.setColor(Color.WHITE);
            font.getData().setScale(2f);
            font.draw(batch, "Level gelöst!", level.width / 2f - 90, level.height - 50);
            font.getData().setScale(1f);
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
