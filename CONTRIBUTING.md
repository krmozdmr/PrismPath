# Mitarbeit an Prism Path

## Branches

| Branch       | Zweck                                                        |
|--------------|--------------------------------------------------------------|
| `main`       | Nur lauffähige, freigegebene Stände (z. B. Meilensteine)     |
| `develop`    | Integrationsbranch; hier landen alle fertigen Features       |
| `feature/*`  | Ein Feature oder eine Komponente, z. B. `feature/ray-tracer` |
| `fix/*`      | Fehlerbehebung, z. B. `fix/mirror-reflection`                |

Ablauf:

1. Von `develop` abzweigen: `git switch develop && git pull && git switch -c feature/<name>`
2. In kleinen, nachvollziehbaren Commits arbeiten.
3. Branch pushen und einen Pull Request nach `develop` öffnen.
4. Mindestens ein anderes Teammitglied prüft den Pull Request.
5. `develop` wird zu jedem Meilenstein per Pull Request nach `main` gebracht und getaggt (`v0.1.0`, …).

Niemand pusht direkt auf `main` oder `develop`.

## Commit-Nachrichten

Wir verwenden [Conventional Commits](https://www.conventionalcommits.org/de/):

```
<typ>(<bereich>): <kurze Beschreibung im Präsens>
```

| Typ        | Wofür                                     |
|------------|-------------------------------------------|
| `feat`     | Neue Funktion                             |
| `fix`      | Fehlerbehebung                            |
| `test`     | Tests hinzufügen oder ändern              |
| `refactor` | Umbau ohne geändertes Verhalten           |
| `docs`     | Dokumentation                             |
| `build`    | Gradle, Abhängigkeiten                    |
| `chore`    | Sonstiges (z. B. `.gitignore`)            |

Der Bereich ist das betroffene Paket: `model`, `light`, `input`, `render`, `level`, `screen`, `ui`, `net`.

Beispiele:

```
feat(light): RayTracer mit Spiegelreflexion
test(model): Kollision zwischen Spieler und Wand
fix(input): Shift-Feindrehung bei Spieler 2
```

## Code-Regeln

- Die Pakete `model` und `light` importieren nichts aus `render`, `screen`, `ui`, `input` oder `net`
  und rufen keine Grafik-, Audio- oder Fenster-APIs auf.
- Neue Spiellogik bekommt Unit-Tests unter `core/src/test/java`.
- Vor jedem Pull Request muss `./gradlew build` ohne Fehler durchlaufen.
- Neue Level sind JSON-Dateien in `levels/` und brauchen keine Code-Änderung.
