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

**R** startet das Level neu, **Enter** führt nach dem Lösen zum nächsten Level.
**Esc** beendet vorerst das Spiel; später öffnet es das Menü.

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

## Neues Level anlegen

Level liegen als JSON-Dateien in `core/src/main/resources/levels/`. Für ein neues Level
braucht es keine Code-Änderung (NFA05):

1. Datei anlegen, z. B. `04-mein-level.json`.
2. Dateinamen in `levels/index.json` an der gewünschten Stelle eintragen.
3. `./gradlew build` prüft, dass alle Level laden.

Koordinaten in Einheiten, Ursprung unten links, Winkel in Grad (0° = rechts, gegen den
Uhrzeigersinn positiv).

```json
{
  "name": "Mein Level",
  "width": 1280,
  "height": 720,
  "border": 20,
  "walls": [ { "x": 760, "y": 260, "w": 20, "h": 200 } ],
  "objects": [
    { "type": "source", "x": 60,   "y": 360, "angle": 0, "color": "WHITE" },
    { "type": "prism",  "x": 320,  "y": 360 },
    { "type": "mirror", "x": 500,  "y": 200, "angle": 45 },
    { "type": "mixer",  "x": 700,  "y": 500, "angle": 0 },
    { "type": "target", "x": 1200, "y": 360, "color": "GREEN" }
  ],
  "players": [ { "x": 160, "y": 220 }, { "x": 160, "y": 500, "facing": 0 } ]
}
```

| Feld | Bedeutung |
|---|---|
| `border` | optional: Rahmen aus Wänden mit dieser Dicke |
| `walls` | Rechtecke mit linker unterer Ecke `x`, `y`, Breite `w`, Höhe `h` |
| `type` | `source`, `mirror`, `prism`, `mixer` oder `target` |
| `color` | bei `source` und `target`: `RED`, `GREEN`, `BLUE`, `YELLOW`, `CYAN`, `MAGENTA`, `WHITE` |
| `movable`, `rotatable` | optional; Standard: Lichtquellen fest, Spiegel/Prismen/Mixer beweglich und drehbar, Ziele fest |
| `players` | eine oder zwei Figuren, `facing` optional |

## Mitarbeit

Branches, Commit-Nachrichten und Pull Requests: siehe [CONTRIBUTING.md](CONTRIBUTING.md).
