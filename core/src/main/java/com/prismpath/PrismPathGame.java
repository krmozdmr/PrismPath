package com.prismpath;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.ScreenUtils;

/**
 * Einstiegspunkt des Spiels. Die Screens (Hauptmenü, Levelauswahl, Spiel)
 * werden in späteren Schritten im Paket {@code screen} ergänzt.
 */
public class PrismPathGame extends Game {

    @Override
    public void create() {
        // Der erste Screen wird gesetzt, sobald das Paket screen existiert.
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.05f, 0.06f, 0.09f, 1f);
        super.render();
    }
}
