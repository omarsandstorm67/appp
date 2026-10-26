# Konzept: Sternenjäger – Weltraum-Shooter

## 1. Definition der Anwendung
**Name:** Sternenjäger
**Idee:** Ein Arcade-Shooter für Android. Du steuerst ein Raumschiff am unteren Bildschirmrand, wischst nach links/rechts, das Schiff feuert automatisch. Außerirdische Angreifer fliegen von oben heran und schießen zurück. Ziel: möglichst viele Aliens abschießen und den Highscore schlagen.
**Kernproblem/Nutzen:** Kurze Spielrunden (1–5 Min.) für Zwischendurch, Ein-Finger-Steuerung, kein Konto, keine Werbung, kein Internet nötig.

## 2. Zielgruppe
- Alter 10–45, Gelegenheitsspieler auf Android-Smartphones, deutschsprachig
- Mögen Retro-Arcade (Space Invaders, Galaga), wollen schnellen Einstieg ohne Tutorial
- Altersfreigabe: Fantasy-Gewalt gegen Aliens (ohne Blut/Realismus), voraussichtlich USK 6 / PEGI 3–7 (Einstufung erfolgt über den IARC-Fragebogen im Play Store)

## 3. Funktionen
### Must-haves (MVP, bereits umgesetzt)
- Raumschiff per Wischen steuern, automatisches Feuern
- Aliens erscheinen laufend, schießen zurück, werden mit der Zeit schneller (Level alle 10 Abschüsse)
- 3 Leben, kurze Unverwundbarkeit nach Treffer
- Punktestand, Level-Anzeige, Game-Over-Bildschirm mit Neustart per Tippen
- Highscore lokal gespeichert (Jetpack DataStore)

### Nice-to-haves (erst nach lauffähigem MVP)
- Sound und Vibration, Explosionsanimationen
- Power-ups (Doppelschuss, Schild, Bombe), Bossgegner alle 5 Level
- Verschiedene Schiffe/Skins, Pause-Menü, Einstellungen
- Google-Play-Games-Bestenliste
- Englische Lokalisierung

## 4. Technische Voraussetzungen
- Plattform: Android (minSdk 26, targetSdk 34), Hochformat, Vertrieb über Google Play Store (AAB)
- Sprache/UI: Kotlin, Jetpack Compose (Canvas + eigene Game-Loop mit `withFrameNanos`), Material 3
- Speicherung: Jetpack DataStore (nur Highscore)
- Build: Gradle (Kotlin DSL), GitHub Actions (Debug-APK + Release-AAB als Artefakte)
- Budget: 0 € (Play-Entwicklerkonto einmalig 25 US-$)

## 5. Store-Checkliste
- [ ] App-Icon (adaptive Icon + 512×512 Store-Icon)
- [ ] Release-Signierung (Keystore als GitHub Secret)
- [ ] Datenschutzerklärung (App sammelt keine Daten)
- [ ] Screenshots, Kurz-/Langbeschreibung, Altersfreigabe (IARC)
- [ ] Tests (Unit-Tests für Spiellogik) ergänzen

## 6. Entscheidungslog
- Compose-Canvas statt Game-Engine: keine Zusatzbibliothek, kleine App, reicht für 2D-Arcade
- Alle Größen relativ zur Bildschirmbreite (1 Einheit = Breite/360), damit es auf allen Geräten gleich aussieht
- Spiellogik (`Game`) getrennt von der Darstellung, damit sie testbar bleibt
- Kein Gradle-Wrapper im Repo: CI installiert Gradle direkt
