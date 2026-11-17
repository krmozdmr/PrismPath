package com.prismpath;

import com.badlogic.gdx.Game;
import com.prismpath.level.DemoLevel;
import com.prismpath.screen.GameScreen;

/** Einstiegspunkt des Spiels. Startet vorerst direkt das Testlevel. */
public class PrismPathGame extends Game {

    @Override
    public void create() {
        setScreen(new GameScreen(DemoLevel::create));
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().dispose();
        }
        super.dispose();
    }
}
