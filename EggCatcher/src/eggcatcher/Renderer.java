package eggcatcher;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Renderer {

    private final int scale;

    private final BufferedImage imgInactiveChicken;
    private final BufferedImage imgActiveChicken;
    private final BufferedImage imgWhiteEgg;
    private final BufferedImage imgGoldenEgg;
    private final BufferedImage imgDropping;
    private final BufferedImage imgBasket;
    private final BufferedImage imgSplatteredEgg;
    private final BufferedImage imgSplatteredDropping;
    private final BufferedImage imgMap;

    private final BitmapFont font;

    private static final Color HUD_TEXT    = new Color(255, 228, 77);
    private static final Color HUD_SHADOW  = new Color(80,  40,  0);

    public Renderer(SpriteAtlas atlas, BitmapFont font, int scale) {
        this.scale = scale;
        this.font  = font;

        imgInactiveChicken   = atlas.get("InactiveChicken");
        imgActiveChicken     = atlas.get("ActiveChicken");
        imgWhiteEgg          = atlas.get("WhiteEgg");
        imgGoldenEgg         = atlas.get("GoldenEgg");
        imgDropping          = atlas.get("ChickenDropping");
        imgBasket            = atlas.get("Basket");
        imgSplatteredEgg     = atlas.get("SplatteredEgg");
        imgSplatteredDropping= atlas.get("SplatteredDropping");
        imgMap               = atlas.get("Map");
    }

    public void render(Graphics2D g, World world) {
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_SPEED);

        Graphics2D sg = (Graphics2D) g.create();
        sg.scale(scale, scale);
        drawBackground(sg);
        drawFallingObjects(sg, world);
        drawChickens(sg, world);
        drawBasket(sg, world);
        sg.dispose();

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        drawHUD(g, world);

        switch (world.getScreen()) {
            case MENU      -> drawMenu(g, world);
            case GAME_OVER -> drawGameOver(g, world);
            default        -> {}
        }
    }

    private void drawBackground(Graphics2D g) {
        g.drawImage(imgMap, 0, 0, null);
    }

    private void drawFallingObjects(Graphics2D g, World world) {
        for (FallingObject obj : world.getObjects()) {
            BufferedImage sprite;
            float drawY = obj.y;

            if (obj.splatting) {
                sprite = (obj.type == 0) ? imgSplatteredDropping : imgSplatteredEgg;
                drawY  = FallingObject.CATCH_Y + 2;
            } else {
                sprite = switch (obj.type) {
                    case 0 -> imgDropping;
                    case 1 -> imgWhiteEgg;
                    default -> imgGoldenEgg;
                };
            }
            g.drawImage(sprite, (int) obj.x, (int) drawY, null);
        }
    }

    private void drawChickens(Graphics2D g, World world) {
        for (Chicken ch : world.getChickens()) {
            BufferedImage sprite = ch.active ? imgActiveChicken : imgInactiveChicken;
            g.drawImage(sprite, (int) ch.x, (int) Chicken.Y, null);
        }
    }

    private void drawBasket(Graphics2D g, World world) {
        Basket b = world.getBasket();
        g.drawImage(imgBasket, (int) b.getLeft(), (int) b.getTop(), null);
    }

    private static final Font HUD_LABEL = new Font(Font.MONOSPACED, Font.BOLD, 12);
    private static final Font HUD_VALUE = new Font(Font.MONOSPACED, Font.BOLD, 17);

    private void drawHUD(Graphics2D g, World world) {
        int sw = GameWindow.WORLD_W * scale;

        String scoreStr = String.valueOf(world.getScore());
        drawHudPair(g, "SCORE", scoreStr, 8, true, sw);

        String livesStr = "x" + world.getLives();
        drawHudPair(g, "LIVES", livesStr, sw - hudPairWidth(g, "LIVES", livesStr) - 8, false, sw);
    }

    private void drawHudPair(Graphics2D g, String label, String value, int x, boolean left, int sw) {
     
        g.setFont(HUD_LABEL);
        FontMetrics lm = g.getFontMetrics();
        int labelY = lm.getAscent() + 5;

        g.setFont(HUD_VALUE);
        FontMetrics vm = g.getFontMetrics();
        int valueY = labelY + vm.getAscent() + 3;

        g.setColor(HUD_SHADOW);
        g.setFont(HUD_LABEL); g.drawString(label, x + 1, labelY + 1);
        g.setFont(HUD_VALUE); g.drawString(value, x + 1, valueY + 1);
        g.setFont(HUD_LABEL); g.setColor(HUD_TEXT);  g.drawString(label, x, labelY);
        g.setFont(HUD_VALUE); g.setColor(Color.WHITE); g.drawString(value, x, valueY);
    }

    private int hudPairWidth(Graphics2D g, String label, String value) {
        g.setFont(HUD_LABEL);
        int lw = g.getFontMetrics().stringWidth(label);
        g.setFont(HUD_VALUE);
        int vw = g.getFontMetrics().stringWidth(value);
        return Math.max(lw, vw);
    }


    private static final Color OVERLAY_BG    = new Color(10,  5, 20, 210);
    private static final Color TITLE_COLOR   = new Color(255, 228,  77);
    private static final Color SUBTITLE_COLOR= new Color(180, 140, 255);
    private static final Color BUTTON_BG     = new Color(255, 228,  77);
    private static final Color BUTTON_FG     = new Color( 20,  10,  40);
    private static final Color WHITE         = Color.WHITE;

    private void drawMenu(Graphics2D g, World world) {
        int sw = GameWindow.WORLD_W * scale;
        int sh = GameWindow.WORLD_H * scale;

        g.setColor(OVERLAY_BG);
        g.fillRect(0, 0, sw, sh);

        String title    = "EGG CATCHER";
        String subtitle = "Move mouse to catch eggs";
        String hint1    = "White egg: +5    Golden egg: +10";
        String hint2    = "Dropping: -5pts  Missed egg: -1 life";
        String btnText  = "CLICK TO PLAY";

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        drawCentred(g, title, sh / 2 - 80, TITLE_COLOR, sw);

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        drawCentred(g, subtitle, sh / 2 - 40, SUBTITLE_COLOR, sw);
        drawCentred(g, hint1,    sh / 2 - 16, WHITE, sw);
        drawCentred(g, hint2,    sh / 2 +  4, WHITE, sw);

        drawButton(g, btnText, sw / 2, sh / 2 + 46, sw);
    }

    private void drawGameOver(Graphics2D g, World world) {
        int sw = GameWindow.WORLD_W * scale;
        int sh = GameWindow.WORLD_H * scale;

        g.setColor(OVERLAY_BG);
        g.fillRect(0, 0, sw, sh);

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        drawCentred(g, "GAME OVER", sh / 2 - 60, TITLE_COLOR, sw);

        g.setFont(new Font("Monospaced", Font.BOLD, 15));
        drawCentred(g, "Score: " + world.getScore(), sh / 2 - 20, WHITE, sw);

        drawButton(g, "CLICK TO PLAY AGAIN", sw / 2, sh / 2 + 30, sw);
    }

    private void drawCentred(Graphics2D g, String text, int y, Color color, int sw) {
        FontMetrics fm = g.getFontMetrics();
        int x = (sw - fm.stringWidth(text)) / 2;
        g.setColor(new Color(0, 0, 0, 160));
        g.drawString(text, x + 2, y + 2);
        g.setColor(color);
        g.drawString(text, x, y);
    }

    private void drawButton(Graphics2D g, String text, int cx, int cy, int sw) {
        g.setFont(new Font("Monospaced", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(text);
        int th = fm.getHeight();
        int pad = 14;
        int bw = tw + pad * 2, bh = th + pad;
        int bx = cx - bw / 2, by = cy - bh / 2;

        g.setColor(BUTTON_BG);
        g.fillRoundRect(bx, by, bw, bh, 8, 8);

        g.setColor(BUTTON_FG);
        g.drawString(text, bx + pad, by + fm.getAscent() + pad / 2);
    }
}
