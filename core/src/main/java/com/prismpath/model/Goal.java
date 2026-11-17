package com.prismpath.model;

/**
 * Ein Ziel im Level. Leuchten alle Ziele, ist das Level gelöst (Frage 5).
 *
 * <p>Die Schnittstelle liegt in {@code model}, damit {@link Level} das
 * Levelende kennt, ohne vom Paket {@code light} abzuhängen.
 */
public interface Goal {

    boolean isLit();
}
