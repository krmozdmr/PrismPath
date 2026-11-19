package com.prismpath.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** Rechteck, das Lichtstrahlen und Figuren stoppt. */
public final class Wall implements Solid {

    public final Rectangle rect;

    public Wall(float x, float y, float width, float height) {
        this.rect = new Rectangle(x, y, width, height);
    }

    @Override
    public boolean pushOut(Vector2 center, float radius) {
        return Collision.pushOut(center, radius, rect);
    }
}
