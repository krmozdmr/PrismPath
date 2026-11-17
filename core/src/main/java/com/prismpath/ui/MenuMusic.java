package com.prismpath.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

/**
 * Menümusik mit gespeicherter Einstellung. Die Musik läuft in Menüs und pausiert
 * im Spiel; ob sie überhaupt läuft, bleibt über Neustarts erhalten.
 */
public final class MenuMusic implements Disposable {

    private static final String PREFS = "prismpath-settings";
    private static final String KEY = "music-enabled";

    private final Preferences prefs;
    private final Music music;
    private boolean enabled;
    private boolean wanted;

    public MenuMusic() {
        prefs = Gdx.app.getPreferences(PREFS);
        enabled = prefs.getBoolean(KEY, true);
        music = Gdx.audio.newMusic(Gdx.files.classpath("audio/menu.wav"));
        music.setLooping(true);
        music.setVolume(0.35f);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void toggle() {
        enabled = !enabled;
        prefs.putBoolean(KEY, enabled).flush();
        apply();
    }

    /** {@code true} in Menüs, {@code false} im Spiel. */
    public void setWanted(boolean wanted) {
        this.wanted = wanted;
        apply();
    }

    private void apply() {
        if (enabled && wanted) {
            music.play();
        } else {
            music.pause();
        }
    }

    @Override
    public void dispose() {
        music.dispose();
    }
}
