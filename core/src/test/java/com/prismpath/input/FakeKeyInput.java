package com.prismpath.input;

import java.util.HashSet;
import java.util.Set;

/** Eingabe für Tests: Aktionen werden direkt gesetzt. */
final class FakeKeyInput implements KeyInput {

    private final Set<String> down = new HashSet<>();

    FakeKeyInput press(int playerId, Action a) {
        down.add(playerId + ":" + a);
        return this;
    }

    FakeKeyInput release(int playerId, Action a) {
        down.remove(playerId + ":" + a);
        return this;
    }

    @Override
    public boolean isDown(int playerId, Action action) {
        return down.contains(playerId + ":" + action);
    }
}
