package com.prismpath.input;

/** Was eine Figur tun kann; welche Taste dazu gehört, entscheidet {@link KeyInput}. */
public enum Action {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    /** Aufnehmen oder Ablegen. */
    GRAB,
    /** Gegen den Uhrzeigersinn drehen. */
    ROTATE_LEFT,
    /** Im Uhrzeigersinn drehen. */
    ROTATE_RIGHT,
    /** Zusammen mit einer Drehtaste: fein drehen. */
    FINE
}
