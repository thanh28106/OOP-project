# Egg Catcher — Java Rebuild

A retro-style 2D arcade game built in Java using custom rendering tools on top of the Java Swing and AWT graphics framework. The game features falling eggs (white and golden) and chicken droppings generated dynamically by overhead chickens. The player controls a ground basket to catch eggs while avoiding dangerous falling debris
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
│   ├── GameConfig.java       ← can change the difficulty based of speed
│   ├── Basket.java
│   ├── Chicken.java
│   ├── FallingObject.java
│   ├── SpriteAtlas.java      ← parses LibGDX .atlas + slices PNG
│   └── BitmapFont.java       ← AngelCode .fnt bitmap font renderer
└── README.md
```

## Requirements

| Requirement | Version |
|-------------|---------|
| Java JDK    | 17 or later (uses records) |

No Maven, no Gradle, no external jars.

## Build & run

### Manual
```bash
javac -d bin src/eggcatcher/*.java
java -cp bin eggcatcher.Main
```

> **Important:** Run from the `EggCatcher/` directory so the `assets/` folder is found.

## Game Controls & Features
Basket Movement: Use the left arrow (<-) and right arrow (->) keys

Menu Control: Use your mouse to click options in the UI

Main Menu: Offers structural preferences into the game 

Retry Screen: Allows players to instantly drop back into a fresh gameplay session upon losing all player lives
## Scoring

| Object | Catch | Miss |
|--------|-------|------|
| ⚪ White Egg   | +5  pts | −1 life |
| 🟡 Golden Egg  | +10 pts | −1 life |
| 💩 Dropping    | −5  pts (−1 life if score hits 0) | nothing |

Start with **5 lives**. Every 50 points the drop rate speeds up.

## Known bug
the hitboxes are not mathematically correct so the egg will eventually drop inside the bag when the bag is close enough
egg being broken after catching into the bag(visual bug)
