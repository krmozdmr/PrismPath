package com.prismpath.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ein Level mit Wänden, Objekten und Figuren. Die Spielfläche reicht von
 * (0, 0) bis ({@link #width}, {@link #height}).
 */
public final class Level {

    public final float width;
    public final float height;

    private final List<Wall> walls = new ArrayList<>();
    private final List<GameObject> objects = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    public Level(float width, float height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Levelgröße muss positiv sein");
        }
        this.width = width;
        this.height = height;
    }

    public Wall addWall(Wall wall) {
        walls.add(wall);
        return wall;
    }

    public <T extends GameObject> T addObject(T object) {
        objects.add(object);
        return object;
    }

    public Player addPlayer(Player player) {
        players.add(player);
        return player;
    }

    public List<Wall> walls() {
        return Collections.unmodifiableList(walls);
    }

    public List<GameObject> objects() {
        return Collections.unmodifiableList(objects);
    }

    public List<Player> players() {
        return Collections.unmodifiableList(players);
    }

    /**
     * {@code true}, wenn das Level mindestens ein Ziel hat und alle Ziele
     * leuchten (Frage 5).
     */
    public boolean isSolved() {
        boolean anyGoal = false;
        for (GameObject o : objects) {
            if (o instanceof Goal) {
                anyGoal = true;
                if (!((Goal) o).isLit()) {
                    return false;
                }
            }
        }
        return anyGoal;
    }

    /** {@code true}, wenn eine Figur dieses Objekt trägt. */
    public boolean isHeld(GameObject object) {
        for (Player p : players) {
            if (p.held == object) {
                return true;
            }
        }
        return false;
    }

    /** Wände und alle Objekte, die gerade nicht getragen werden. */
    public List<Solid> solidObjects() {
        List<Solid> result = new ArrayList<>(walls);
        for (GameObject o : objects) {
            if (!isHeld(o)) {
                result.add(o);
            }
        }
        return result;
    }

    /** Alles, was {@code player} blockiert: {@link #solidObjects()} und die anderen Figuren. */
    public List<Solid> solidsFor(Player player) {
        List<Solid> result = solidObjects();
        for (Player p : players) {
            if (p != player) {
                result.add(p);
            }
        }
        return result;
    }
}
