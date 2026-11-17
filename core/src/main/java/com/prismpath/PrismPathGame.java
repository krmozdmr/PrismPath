package com.prismpath;

import com.badlogic.gdx.Game;
import com.prismpath.level.LevelCatalog;
import com.prismpath.screen.GameScreen;

/** Einstiegspunkt des Spiels. Startet vorerst direkt das erste Level. */
public class PrismPathGame extends Game {

    @Override
    public void create() {
        LevelCatalog catalog = LevelCatalog.load();
        setScreen(new GameScreen(() -> catalog.create(0)));
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        super.dispose();
    }
}
