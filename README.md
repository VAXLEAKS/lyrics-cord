# Lyrics Cord — Source

Fabric-Mod für Minecraft **1.21.11**: Spotify-Lyrics in 3D im Himmel, Sky-Presets mit Wetter, HUD-Leiste und Glas-Hotbar. Client-only.

Dieses Repository ist ein aus dem Release-Jar rekonstruiertes Quellprojekt (Vineflower 1.12.0, Yarn-Mappings). Es baut mit Fabric Loom und erzeugt ein installierbares Mod-Jar.

## Bauen

Voraussetzungen:

- JDK 21 oder neuer
- Internet beim ersten Build (Gradle lädt Minecraft, Yarn und Fabric API)

```bash
# Windows
gradlew.bat build

# Linux / macOS
./gradlew build
```

Ergebnis:

```
build/libs/neonlyrics-4.8.3+mc1.21.11.jar
```

Das Jar ist bereits für den Produktionseinsatz remapped (Intermediary-Namen) und kann direkt in `mods/` gelegt werden.

## Versionen

| Property | Wert |
|---|---|
| Minecraft | 1.21.11 |
| Yarn | 1.21.11+build.6 |
| Fabric Loader | 0.19.3 |
| Fabric API | 0.141.6+1.21.11 |
| Loom | 1.19.3 |
| Java | 21 |

Alle Werte stehen in `gradle.properties`.

## Struktur

```
src/main/java/dev/neonlyrics/
  NeonLyrics.java        Mod-Entrypoint, Keybinds, Config
  Beat.java              Track-/Beat-Erkennung (früher: Lizenzprüfung)
  config/                Config + Storage
  lyrics/                Spotify-, LRC- und NetEase-Anbindung
  hud/                   HUD-Elemente, Hotbar, Crosshair, Cover
  gui/                   Lyrics-Overlay, Einstellungen, Sky-Studio
  sky/                   Sky-Renderer, Presets, Shader, Panoramen
  cosmetic/              Todes-Effekte, Autos, Emotes, Accessories
  mixin/                 Mixins (Hand, Buch, Wolken, Fog, EntityRenderer)

src/main/resources/
  assets/neonlyrics/     Texturen, Modelle, Shader, Fonts, Lang
  fabric.mod.json
  neonlyrics.mixins.json
  neonlyrics.accesswidener
```

## Hinweise zum Quellcode

Der Code ist dekompiliert, keine Original-Quellen. Variablen heißen daher `var0`, `var1` usw. Folgende Dekompilier-Artefakte wurden korrigiert:

- `Beat.rd()`: `for (byte …)`-Zähler lief bei 128 über → `int`
- gleiche Byte-Zähler in `DeathCards`, `Cover`, `Gfx`, `SportCar`, `SkyPainter`, `SpotifyTutorialScreen`
- Rohlists ohne Generics ergänzt (`LyricsProvider`, `LyricTiming`, `FloatWords`, `SkyPreset`, `DeathFx`, `WorldSky`, `Emotes`, `SportCar`, `CarGpu`, `SkyImages`)
- verdeckte Lambda-Variablen in `LyricsScreen` (falsche `Runnable`-Referenzen, falscher `pressed`-Key)

Die Mixin-Ziele in den Annotationen stehen absichtlich in Intermediary-Namen (`method_3231` usw.), damit sie im gebauten Jar ohne Remap-Sonderfall funktionieren.

Die frühere Lizenzsperre (`Beat.live()`, `Beat.sync()`, `Beat.owner()`) ist neutralisiert und gibt immer `true` zurück. Signaturprüfung, Owner-UUID und Redeem-Hashes sind noch als toter Code vorhanden, werden aber von den Gates nicht mehr aufgerufen.

## Credits

Original-Mod: **polishtraps85** (`neonlyrics`, MIT). Dieses Repository enthält keinen Code des ursprünglichen Autors im Sinne einer offiziellen Quelle, sondern eine Rekonstruktion des veröffentlichten Builds.

## GitHub

```bash
cd lyrics-cord
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/<dein-user>/lyrics-cord.git
git push -u origin main
```

Der Workflow in `.github/workflows/build.yml` baut bei jedem Push automatisch ein Jar und legt es als Artifact ab.
