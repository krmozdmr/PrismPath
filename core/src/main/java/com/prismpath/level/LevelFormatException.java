package com.prismpath.level;

/** Eine Level-Datei ist kein gültiges Level. Die Nachricht nennt die Stelle. */
public final class LevelFormatException extends RuntimeException {

    public LevelFormatException(String message) {
        super(message);
    }

    public LevelFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
