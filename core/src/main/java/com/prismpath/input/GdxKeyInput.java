package com.prismpath.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import java.util.EnumMap;
import java.util.Map;

/**
 * Tastatur über libGDX mit der Belegung aus Frage 8.
 *
 * <p>Spieler 1: W A S D, Leertaste, Q / E, linke Shift-Taste.
 * Spieler 2: Pfeiltasten, M, Ö / Ç, rechte Shift-Taste. libGDX benennt Tasten
 * nach ihrer Lage auf einer US-Tastatur; Ö und Ç der türkischen Q-Tastatur
 * liegen dort, wo auf der US-Tastatur Komma und Punkt liegen.
 */
public final class GdxKeyInput implements KeyInput {

    private final Map<Action, int[]> player1 = new EnumMap<>(Action.class);
    private final Map<Action, int[]> player2 = new EnumMap<>(Action.class);

    public GdxKeyInput() {
        player1.put(Action.UP, keys(Keys.W));
        player1.put(Action.DOWN, keys(Keys.S));
        player1.put(Action.LEFT, keys(Keys.A));
        player1.put(Action.RIGHT, keys(Keys.D));
        player1.put(Action.GRAB, keys(Keys.SPACE));
        player1.put(Action.ROTATE_LEFT, keys(Keys.Q));
        player1.put(Action.ROTATE_RIGHT, keys(Keys.E));
        player1.put(Action.FINE, keys(Keys.SHIFT_LEFT));

        player2.put(Action.UP, keys(Keys.UP));
        player2.put(Action.DOWN, keys(Keys.DOWN));
        player2.put(Action.LEFT, keys(Keys.LEFT));
        player2.put(Action.RIGHT, keys(Keys.RIGHT));
        player2.put(Action.GRAB, keys(Keys.M));
        player2.put(Action.ROTATE_LEFT, keys(Keys.COMMA));   // Ö
        player2.put(Action.ROTATE_RIGHT, keys(Keys.PERIOD)); // Ç
        player2.put(Action.FINE, keys(Keys.SHIFT_RIGHT));
    }

    @Override
    public boolean isDown(int playerId, Action action) {
        Map<Action, int[]> map = playerId == 1 ? player1 : playerId == 2 ? player2 : null;
        if (map == null) {
            return false;
        }
        for (int key : map.get(action)) {
            if (Gdx.input.isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }

    private static int[] keys(int... keys) {
        return keys;
    }
}
