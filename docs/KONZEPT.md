# Konzept: Schluck – Trink-Tracker

## 1. Definition der Anwendung
**Name:** Schluck
**Idee:** Eine schlanke Android-App, die täglich getrunkene Wassermenge erfasst und den Fortschritt zum Tagesziel zeigt. Ein Tipp = ein Glas. Kein Konto, keine Werbung, alle Daten bleiben auf dem Gerät.
**Kernproblem:** Viele Menschen trinken zu wenig und vergessen, wie viel sie getrunken haben. Bestehende Apps sind oft überladen oder verlangen Registrierung.

## 2. Zielgruppe
- Alter 16–60, Smartphone-Nutzer (Android), deutschsprachig
- Gesundheitsbewusste Menschen, Büroarbeiter, Sportler, Menschen mit Trinkziel (z. B. ärztlich empfohlen)
- Wollen Erfassung in unter 3 Sekunden, ohne Account und ohne Datenweitergabe

## 3. Funktionen
### Must-haves (MVP)
- Tagesziel festlegen (Standard 2000 ml, änderbar)
- Schnell-Buttons: +150 / +250 / +500 ml
- Fortschrittsanzeige (Ring + Prozent + ml)
- Rückgängig-Funktion (letzte Menge abziehen) und Tages-Reset
- Lokale Speicherung (Jetpack DataStore), automatischer Neustart des Zählers am nächsten Tag

### Nice-to-haves (erst nach lauffähigem MVP)
- Erinnerungs-Benachrichtigungen (Intervall, Ruhezeiten)
- Verlauf / Wochenstatistik mit Diagramm
- Eigene Glasgrößen, Home-Screen-Widget
- Dark-Mode-Feinschliff, Mehrsprachigkeit (EN)
- Optional: Health Connect-Anbindung

## 4. Technische Voraussetzungen
- Plattform: Android (minSdk 26, targetSdk 34), Vertrieb über Google Play Store (AAB-Format)
- Sprache/UI: Kotlin, Jetpack Compose, Material 3
- Speicherung: Jetpack DataStore (lokal, keine Server)
- Build: Gradle (Kotlin DSL), GitHub Actions (Debug-APK + Release-AAB als Artefakte)
- Budget: 0 € (Google-Play-Entwicklerkonto einmalig 25 US-$ für Veröffentlichung)

## 5. Store-Checkliste
- [ ] App-Icon (adaptive, 512×512 für Store) ergänzen
- [ ] Release-Signierung (Keystore als GitHub Secret)
- [ ] Datenschutzerklärung (App sammelt keine Daten)
- [ ] Store-Eintrag: Screenshots, Kurz-/Langbeschreibung
- [ ] Tests (Unit + UI) ergänzen

## 6. Entscheidungslog
- Compose statt XML-Views: moderner, weniger Code
- DataStore statt Room: MVP braucht nur 3 Werte; Room erst für Verlauf
- Kein Gradle-Wrapper im Repo nötig: CI installiert Gradle direkt (Wrapper später per `gradle wrapper` ergänzbar)
