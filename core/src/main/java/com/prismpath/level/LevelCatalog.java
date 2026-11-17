package com.prismpath.level;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.prismpath.model.Level;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Alle Level aus dem Ordner {@code levels/} in der Reihenfolge von
 * {@code levels/index.json} (NFA05). Ein neues Level ist eine JSON-Datei plus
 * eine Zeile im Index, ohne Code-Änderung.
 *
 * <p>Jeder Aufruf von {@link #create(int)} liefert ein frisches Level, damit
 * ein Neustart den Ausgangszustand wiederherstellt.
 */
public final class LevelCatalog {

    public static final String FOLDER = "levels/";

    private final List<String> files;
    private final List<String> json;
    private final List<String> names;

    private LevelCatalog(List<String> files, List<String> json) {
        this.files = Collections.unmodifiableList(files);
        this.json = json;
        List<String> n = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String name = LevelLoader.parse(json.get(i)).name;
            n.add(name.isEmpty() ? files.get(i) : name);
        }
        this.names = Collections.unmodifiableList(n);
    }

    /** Liest Index und Level vom Klassenpfad und prüft jedes Level einmal. */
    public static LevelCatalog load() {
        JsonValue index = new JsonReader().parse(read(FOLDER + "index.json"));
        if (index == null || !index.isArray() || index.size == 0) {
            throw new LevelFormatException("levels/index.json muss eine nicht leere Liste sein");
        }
        List<String> files = new ArrayList<>();
        List<String> json = new ArrayList<>();
        for (JsonValue entry : index) {
            String file = entry.asString();
            files.add(file);
            String text = read(FOLDER + file);
            try {
                LevelLoader.parse(text);
            } catch (LevelFormatException e) {
                throw new LevelFormatException(file + ": " + e.getMessage(), e);
            }
            json.add(text);
        }
        return new LevelCatalog(files, json);
    }

    public int size() {
        return files.size();
    }

    public String file(int index) {
        return files.get(index);
    }

    public String name(int index) {
        return names.get(index);
    }

    public Level create(int index) {
        return LevelLoader.parse(json.get(index));
    }

    private static String read(String path) {
        try (InputStream in = LevelCatalog.class.getClassLoader().getResourceAsStream(path)) {
            if (in == null) {
                throw new LevelFormatException("Datei fehlt: " + path);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new LevelFormatException("Datei nicht lesbar: " + path, e);
        }
    }
}
