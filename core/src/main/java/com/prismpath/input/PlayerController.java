package com.prismpath.input;

import com.badlogic.gdx.math.MathUtils;
import com.prismpath.model.Level;
import com.prismpath.model.Player;
import java.util.HashMap;
import java.util.Map;

/**
 * Setzt die Eingabe in Bewegung, Aufnehmen/Ablegen und Drehen um (FA02, FA04).
 *
 * <p>Drehen ist stufenlos (Frage 2): gehaltene Drehtasten beschleunigen in
 * {@value #RAMP_SECONDS} s von {@value #MIN_SPEED} auf {@value #MAX_SPEED} °/s,
 * mit Shift dreht man fein mit {@value #FINE_SPEED} °/s.
 */
public final class PlayerController {

    public static final float MIN_SPEED = 20f;
    public static final float MAX_SPEED = 120f;
    public static final float FINE_SPEED = 15f;
    public static final float RAMP_SECONDS = 1f;

    /** Zustand pro Figur zwischen zwei Frames. */
    private static final class State {
        boolean grabWasDown;
        float rotateHeld;
    }

    private final Map<Integer, State> states = new HashMap<>();

    public void update(Level level, KeyInput input, float dt) {
        for (Player p : level.players()) {
            State s = states.computeIfAbsent(p.id, id -> new State());
            move(p, level, input, dt);
            grab(p, s, level, input);
            rotate(p, s, level, input, dt);
        }
    }

    private static void move(Player p, Level level, KeyInput in, float dt) {
        float dx = axis(in, p.id, Action.RIGHT, Action.LEFT);
        float dy = axis(in, p.id, Action.UP, Action.DOWN);
        p.move(dx, dy, dt, level);
    }

    /** Aufnehmen und Ablegen nur beim Drücken, nicht solange die Taste gehalten wird. */
    private static void grab(Player p, State s, Level level, KeyInput in) {
        boolean down = in.isDown(p.id, Action.GRAB);
        if (down && !s.grabWasDown) {
            if (p.isHolding()) {
                p.drop(level);
            } else {
                p.pickUp(level);
            }
        }
        s.grabWasDown = down;
    }

    /** Dreht das getragene Objekt oder, ohne Objekt in der Hand, das nächste vor der Figur. */
    private static void rotate(Player p, State s, Level level, KeyInput in, float dt) {
        float sign = axis(in, p.id, Action.ROTATE_LEFT, Action.ROTATE_RIGHT);
        if (sign == 0) {
            s.rotateHeld = 0;
            return;
        }
        float speed;
        if (in.isDown(p.id, Action.FINE)) {
            speed = FINE_SPEED;
        } else {
            float ramp = Math.min(s.rotateHeld / RAMP_SECONDS, 1f);
            speed = MathUtils.lerp(MIN_SPEED, MAX_SPEED, ramp);
        }
        s.rotateHeld += dt;
        p.rotate(sign * speed * dt, level);
    }

    private static float axis(KeyInput in, int id, Action positive, Action negative) {
        return (in.isDown(id, positive) ? 1 : 0) - (in.isDown(id, negative) ? 1 : 0);
    }
}
