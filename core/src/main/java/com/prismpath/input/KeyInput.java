package com.prismpath.input;

/**
 * Liefert, welche Aktionen eine Figur gerade ausführt.
 *
 * <p>Lokal steckt dahinter die Tastatur. Im Online-Spiel bekommt der Host die
 * Tasten des Gasts über dieselbe Schnittstelle ({@code RemoteKeyInput}),
 * daher bleibt die Spiellogik gleich (Abb. 2).
 */
public interface KeyInput {

    /**
     * @param playerId Figur, 1 oder 2
     * @return {@code true}, solange die Aktion gedrückt ist
     */
    boolean isDown(int playerId, Action action);
}
