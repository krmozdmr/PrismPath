# Prism Path

Ein kooperatives 2-Spieler-Rätselspiel in Draufsicht, entwickelt mit Java und libGDX.
Die Spieler bewegen sich frei durch das Level, tragen und drehen Spiegel und Prismen und
lenken das Licht so, dass jedes Ziel in genau seiner Farbe leuchtet.

> **Stand:** erste Sprints. Grundlage ist der spielbare Prototyp. Teleporter (FA05) und
> Online-Spiel (FA06) sind entworfen, aber noch nicht umgesetzt.

## Architektur

Grundregel: **Alle Komponenten hängen von der Spiellogik ab, nie umgekehrt.**
Die Spiellogik braucht kein Fenster und wird mit Unit-Tests geprüft.

| Komponente          | Paket            | Aufgabe                                                  |
|---------------------|------------------|----------------------------------------------------------|
| Screens             | `screen`, `ui`   | Spielschleife; Hauptmenü, Levelauswahl und Spiel         |
| Eingabe             | `input`          | Liest die Tastatur; bewegt Figuren, trägt/dreht Objekte  |
| Spiellogik          | `model`, `light` | Level mit allen Objekten; Lichtstrahlen, Farben, Levelende |
| Darstellung         | `render`         | Zeichnet Level, Strahlen und Figuren; ändert nie den Zustand |
| Level-Verwaltung    | `level`          | Liest Level aus JSON-Dateien im Ordner `levels/`         |
| Netzwerk (geplant)  | `net`            | Online-Spiel im LAN über KryoNet                         |

Alle Pakete liegen unter `com.prismpath`.

## Steuerung

| Aktion              | Spieler 1 | Spieler 2   |
|---------------------|-----------|-------------|
| Bewegen             | W A S D   | Pfeiltasten |
| Aufnehmen / Ablegen | Leertaste | M           |
| Drehen              | Q / E     | Ö / Ç       |
| Fein drehen         | + Shift   | + Shift     |

**R** startet das Level neu, **Esc** öffnet das Menü.

## Voraussetzungen

- Java JDK 11 oder neuer (empfohlen: JDK 21)
- Git

Gradle muss nicht installiert sein, der Wrapper liegt im Projekt.

## Bauen und Starten

```bash
git clone https://github.com/krmozdmr/PrismPath.git
cd PrismPath
./gradlew build        # kompilieren und Tests ausführen
./gradlew lwjgl3:run   # Spiel starten
```

Unter Windows `gradlew.bat` statt `./gradlew` verwenden.

## Projektstruktur

```
PrismPath/
├── core/      Spiellogik, Darstellung, Screens (plattformunabhängig)
├── lwjgl3/    Desktop-Starter
└── gradle/    Gradle-Wrapper
```

## Mitarbeit

Branches, Commit-Nachrichten und Pull Requests: siehe [CONTRIBUTING.md](CONTRIBUTING.md).
