# SafeNet — requirements.md

## 1. Kurzbeschreibung

**SafeNet** ist eine mobile/web-basierte Anwendung, die in Katastrophenfällen (z. B. Überschwemmung, Großbrand) sichere Evakuierungsrouten anzeigt, basierend auf einer Kombination aus **amtlichen Warnungen**, **Live-Traffic-Daten**, **Crowd-Reports** und einem **Katastrophen-RSS-Feed** mit Echtzeitinformationen über Umwelt- und Gefahrenlagen.

Darüber hinaus können Betroffene ihren **Status** mitteilen („Ich bin sicher“ / „Ich brauche Hilfe“), ihren **letzten bekannten Standort** mit Rettungskräften teilen sowie **Bedarfe** (z. B. Medikamente, Wasser, Nahrung) angeben. Familienmitglieder können gegenseitig ihre Position und ihren Zustand sehen. Damit unterstützt SafeNet sowohl die **sichere Evakuierung** als auch die **Rettung und Koordination** in Krisensituationen.

---

## 2. Ziele (Goals)

* Schnelle, verständliche und sichere Routen für Betroffene anbieten.
* Realtime-Situation durch Kombination mehrerer Datenquellen abbilden.
* **Katastrophenwarnungen aus offiziellen RSS-Feeds einbinden** (z. B. DWD, MeteoAlarm).
* Status- und Hilferufe von Betroffenen sichtbar machen.
* Letzte bekannte Standorte von Betroffenen für Helfer zugänglich machen.
* Familienmitglieder gegenseitig lokalisierbar machen.
* Einfache, überzeugende Hackathon-Demo: Report oder Hilferuf → Heatmap → Route passt sich an.

---

## 3. Umfang / Scope

### MVP (24h Hackathon)

* Web-Frontend (React) mit interaktiver Karte (Leaflet/Mapbox).
* Backend (Spring Boot oder Node) mit Endpoints für Reports, Status und Routing.
* Supabase als Datenbank (`reports`, `cells`, `status`).
* Nutzer können Reports und Statusmeldungen erstellen.
* Routing (Mapbox/OSRM) mit Gewichtung durch Gefahren, Traffic, Crowd-Reports.
* **Integration eines Katastrophen-RSS-Feeds (z. B. DWD, MeteoAlarm oder GDACS)**, um aktuelle Warnungen einzublenden.
* Echtzeit-Demo mit simulierten Reports, RSS-Warnungen und Statusmeldungen.

### Nicht-MVP / spätere Features

* Offline-Karten & Routing.
* Push-Notifications für Helfer.
* KI-gestützte Foto-Verifikation.
* Integration offizieller Rettungsdienste.
* Erweiterte Familien-/Gruppenfunktionen.

---

## 4. Stakeholder

* Endnutzer (Betroffene).
* Familienmitglieder und Freunde.
* Helfer/Volunteers.
* Behörden & Rettungsdienste (für Erweiterung).
* Hackathon-Jury / Publikum (Demo-Empfänger).
* Entwicklungsteam.

---

## 5. Functional Requirements

### 5.1 Map & UI

* FR-1: Karte mit Basiskarte (OpenStreetMap/Mapbox).
* FR-2: Layer für Gefahren (Polygone/Heatmap), Traffic (Incidents) und Crowd-Reports (Marker/Heatmap).
* FR-3: Anzeige der eigenen Position (optional, mit Opt-In).
* FR-4: Anzeige von Familien-/Freundesstandorten (optional).
* **FR-4a: Anzeige offizieller Katastrophenwarnungen aus RSS-Feed (Marker oder Flächenlayer).**

### 5.2 Reports & Status

* FR-5: Nutzer kann einen Report erstellen: Kategorie („Überflutung“, „Baum auf Straße“), Foto (optional), Standort.
* FR-6: Reports werden in Supabase gespeichert und erscheinen in Echtzeit auf der Karte.
* FR-7: Nutzer kann Status setzen: **Grün = sicher**, **Rot = brauche Hilfe**.
* FR-8: Nutzer kann zusätzliche Bedarfe melden („Medikamente“, „Wasser“, „Essen“).
* FR-9: Letzter bekannter Standort eines Hilferufs (rote Flagge) ist für Helfer sichtbar.

### 5.3 Routing / SafeNet

* FR-10: Nutzer gibt Start/Ziel an, System berechnet Route.
* FR-11: Route berücksichtigt Hazard-, Traffic-, Crowd- und **RSS-Warnungs-Scores**.
* FR-12: Route passt sich bei neuen Reports/Statusmeldungen automatisch an.

### 5.4 Echtzeit & Demo

* FR-13: Live-Aktualisierung der Karte bei Reports, RSS-Warnungen und Statusmeldungen.
* FR-14: Demo-Mode: Simulation von Gefahren/Reports/Status/Warnungen für Pitch.

---

## 6. Non-Functional Requirements

* NFR-1: Reaktionszeit < 5 s für Updates und Re-Routing.
* NFR-2: Stabiles Verhalten für Demo mit < 100 gleichzeitigen Nutzern.
* NFR-3: Standortnutzung nur per Opt-In; Speicherung gerundet/aggregiert.
* NFR-4: UI leicht verständlich, für Jury auch aus Distanz erkennbar.
* **NFR-5: RSS-Feed wird mindestens alle 60 Sekunden aktualisiert (Polling oder Webhook).**

---

## 7. Datenquellen & Integrationen

* Autoritative Warnungen: **DWD OpenData**, **MeteoAlarm**, **GDACS** (RSS/GeoRSS).
* Traffic: Waze GeoRSS, TomTom oder Mapbox Traffic. Falls nicht verfügbar: Simulation.
* Crowd: eigene Reports-API (Supabase) + Statusmeldungen.
* **RSS-Integration:** Parser für Katastrophenfeeds, der Warnungen in JSON-Format umwandelt und auf Karte darstellt.

---

## 8. Datenschutz & Sicherheit

* Standortdaten nur mit Einwilligung; Speicherung mit begrenzter Genauigkeit.
* Daten nur temporär speichern (TTL z. B. 24–48 h).
* Fotos nur optional und mit Zustimmung.
* Backend-Endpunkte mit einfachen Tokens abgesichert.
* **RSS-Daten nur lesend verarbeitet; keine persönlichen Daten enthalten.**

---

## 9. UX / UI Anforderungen

* Großes Karten-Canvas, klare Farben (Grün = sicher, Rot = Gefahr/Hilfe, Gelb = Warnung).
* Reports mit minimalem Aufwand (2 Klicks).
* Status-Buttons: **„Ich bin sicher“**, **„Ich brauche Hilfe“**.
* Optional: Familienansicht.
* **RSS-Warnungen** erscheinen als **gelbe Zonen oder Icons** mit Tooltip („DWD: Hochwasserwarnung“).
* „Simulate“-Button für Live-Demo.
* Responsive: Desktop-first für Pitch.

---

## 10. API Endpoints (Beispiel)

* `POST /api/reports` — Erstelle Report `{ id, lat, lon, category, photoUrl?, message?, ts }`.
* `GET /api/reports?bbox=...` — Hole Reports im Kartenausschnitt.
* `POST /api/status` — Setze Status `{ userId, lat, lon, flag, needs[], ts }`.
* `GET /api/status?bbox=...` — Hole Statusmeldungen im Kartenausschnitt.
* `POST /api/simulate` — Simuliere Gefahren/Statusmeldungen.
* `POST /api/route` — Berechne Route.
* **`GET /api/alerts` — Hole aktuelle RSS-Warnungen (gefiltert nach Region oder Bounding Box).**

---

## 11. DB Schema (Supabase)

* `reports`

  * id (text PK)
  * category (text)
  * lat (numeric)
  * lon (numeric)
  * photo_url (text)
  * message (text)
  * ts (timestamp)

* `cells` (Crowd Density)

  * cell_id (text PK)
  * count (int)
  * last_update (timestamp)

* `status`

  * user_id (text PK)
  * lat (numeric)
  * lon (numeric)
  * flag (enum: "SAFE" | "HELP")
  * needs (array[text])
  * last_seen (timestamp)

* **`alerts`**

  * id (text PK)
  * source (text) — z. B. "DWD"
  * title (text)
  * description (text)
  * lat (numeric)
  * lon (numeric)
  * severity (enum: "MINOR" | "MODERATE" | "SEVERE")
  * expires (timestamp)

---

## 12. Tech Stack Empfehlung

* Frontend: React + Leaflet oder Mapbox GL.
* Backend: Spring Boot oder Node/Express.
* DB: Supabase (Postgres).
* Routing: OSRM oder Mapbox Directions API.
* **RSS-Verarbeitung:** node-feedparser oder Python feedparser.
* Hosting: Vercel/Netlify (Frontend), Railway/Heroku (Backend).

---

## 13. Akzeptanzkriterien (Definition of Done)

* Karte lädt Basiskarte.
* Nutzer können Reports & Statusmeldungen erstellen.
* Reports, **RSS-Warnungen** & Status erscheinen live auf Karte.
* Routing reagiert sichtbar auf neue Reports/Status/Warnungen.
* Demo-Szenario reproduzierbar in 2–3 Minuten.

---

## 14. Zeitplan (24h)

**0–2h:** Setup (Repo, Supabase Schema, Map UI).
**2–6h:** Reports & Status Endpoints, Frontend-Integration.
**6–10h:** Integration Datenquelle (RSS) oder Simulation.
**10–14h:** Routing-Integration.
**14–18h:** Realtime Updates + Demo-Mode.
**18–22h:** UI Polish + Pitch-Slides.
**22–24h:** General Rehearsal + Deploy.

---

## 15. Demo Script

1. Kurz-Intro (20 s): Problem + Lösung (SafeNet).
2. Live-Demo (60–80 s):

   * Route berechnen.
   * Teammitglied setzt Report („Überflutung“) → Heatmap + Route ändern sich.
   * RSS-Warnung („DWD: Hochwasser“) erscheint → Route weicht aus.
   * Anderes Teammitglied setzt Status „Hilfe benötigt“ → rote Flagge erscheint.
   * Optional: Familienansicht.
3. Abschluss (20–30 s): Impact & Ausblick.

---

## 16. Risiken & Mitigation

* Risiko: Keine API verfügbar → Mitigation: Simulation oder RSS-Feed-Mock.
* Risiko: Datenschutz → Mitigation: Aggregation & TTL.
* Risiko: Live-Demo-Fehlschlag → Mitigation: Video-Backup.

---

## 17. Appendix / Links

* Leaflet: [https://leafletjs.com/](https://leafletjs.com/)
* Supabase: [https://supabase.com/docs](https://supabase.com/docs)
* Waze GeoRSS: [https://www.waze.com/ccp](https://www.waze.com/ccp)
* DWD Open Data: [https://opendata.dwd.de/](https://opendata.dwd.de/)
* MeteoAlarm RSS: [https://feeds.meteoalarm.org/](https://feeds.meteoalarm.org/)
* GDACS Global Alerts: [https://www.gdacs.org/rss.aspx](https://www.gdacs.org/rss.aspx)
