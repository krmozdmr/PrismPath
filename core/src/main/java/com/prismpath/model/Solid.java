package com.prismpath.model;

import com.badlogic.gdx.math.Vector2;

/** Etwas, das Figuren nicht durchlaufen können. */
public interface Solid {

    /**
     * Schiebt einen Kreis aus diesem Hindernis heraus.
     *
     * @return {@code true}, wenn {@code center} verschoben wurde
     */
    boolean pushOut(Vector2 center, float radius);
}
