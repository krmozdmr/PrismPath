package com.prismpath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.prismpath.level.LevelCatalog;
import com.prismpath.screen.GameScreen;
import com.prismpath.screen.LevelSelectScreen;
import com.prismpath.screen.MenuScreen;
import com.prismpath.ui.MenuMusic;

/** Einstiegspunkt des Spiels; wechselt zwischen Menü, Levelauswahl und Spiel. */
public class PrismPathGame extends Game {

    private LevelCatalog catalog;
    private MenuMusic music;

    @Override
    public void create() {
        catalog = LevelCatalog.load();
        music = new MenuMusic();
        showMenu();
    }

    public LevelCatalog catalog() {
        return catalog;
    }

    public MenuMusic music() {
        return music;
    }

    public void showMenu() {
        change(new MenuScreen(this), true);
    }

    public void showLevelSelect() {
        change(new LevelSelectScreen(this), true);
    }

    public void startLevel(int index) {
        change(new GameScreen(this, index), false);
    }

    private void change(Screen next, boolean menuMusic) {
        Screen previous = getScreen();
        setScreen(next);
        if (previous != null) {
            previous.dispose();
        }
        music.setWanted(menuMusic);
    }

    @Override
    public void dispose() {
        if (getScreen() != null) {
            getScreen().hide();
            getScreen().dispose();
        }
        if (music != null) {
            music.dispose();
        }
    }
}
