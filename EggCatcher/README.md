# Egg Catcher — Java Rebuild

A complete rewrite of the original LibGDX Egg Catcher game using plain
**Java 2D** (`javax.swing` + `java.awt`) — no external dependencies.

## Project structure

```
EggCatcher/
├── assets/                   ← original sprites & font (keep here!)
│   ├── egg-catcher.atlas
│   ├── egg-catcher.png
│   ├── BoxyBold.fnt
│   └── BoxyBold.png
├── src/eggcatcher/
│   ├── Main.java             ← entry point
│   ├── GameWindow.java       ← JFrame + constants
│   ├── GamePanel.java        ← game loop, input, double-buffered paint
│   ├── World.java            ← all game logic (no rendering)
│   ├── Renderer.java         ← all rendering (no logic)
│   ├── Basket.java
│   ├── Chicken.java
│   ├── FallingObject.java
│   ├── SpriteAtlas.java      ← parses LibGDX .atlas + slices PNG
│   └── BitmapFont.java       ← AngelCode .fnt bitmap font renderer
├── compile-and-run.sh        ← Linux/macOS build script
├── compile-and-run.bat       ← Windows build script
└── README.md
```

## Requirements

| Requirement | Version |
|-------------|---------|
| Java JDK    | 17 or later (uses records) |

No Maven, no Gradle, no external jars.

## Build & run

### Linux / macOS
```bash
chmod +x compile-and-run.sh
./compile-and-run.sh
```

### Windows
```
compile-and-run.bat
```

### Manual
```bash
mkdir out
javac -d out $(find src -name "*.java")
java -cp out eggcatcher.Main
```

> **Important:** Run from the `EggCatcher/` directory so the `assets/` folder is found.

## Controls

| Input | Action |
|-------|--------|
| Mouse move | Move basket left / right |
| Click | Start / restart game |

## Scoring

| Object | Catch | Miss |
|--------|-------|------|
| ⚪ White Egg   | +5  pts | −1 life |
| 🟡 Golden Egg  | +10 pts | −1 life |
| 💩 Dropping    | −5  pts (−1 life if score hits 0) | nothing |

Start with **5 lives**. Every 50 points the drop rate speeds up.

## Architecture notes

- `World` is purely logical — it has no `Graphics` imports.
- `Renderer` is purely visual — it reads from `World` but never mutates it.
- `GamePanel` owns the **fixed-timestep game loop** on a daemon thread.
- `SpriteAtlas` parses the original LibGDX atlas file (top-left origin, no y-flip needed for Java ImageIO).
- `BitmapFont` parses the AngelCode `.fnt` format and renders glyphs using `Graphics2D.drawImage`.
- All scaling is pixel-perfect nearest-neighbour at `SCALE = 3×`.
