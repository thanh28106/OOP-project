package eggcatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class GamePanel extends JPanel implements MouseMotionListener, MouseListener {

    private static final int TARGET_FPS    = 60;
    private static final long FRAME_NS     = 1_000_000_000L / TARGET_FPS;

    private final World    world;
    private Renderer       renderer;

    private Image   backBuffer;
    private Graphics2D bbg;

    public GamePanel() {
        int w = GameWindow.WORLD_W * GameWindow.SCALE;
        int h = GameWindow.WORLD_H * GameWindow.SCALE;
        setPreferredSize(new Dimension(w, h));
        setBackground(Color.BLACK);
        setFocusable(true);
        requestFocusInWindow();

        world = new World();
        world.setListener(finalScore -> {});

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
            });
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) world.setMovingLeft(true);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) world.setMovingRight(true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_LEFT) world.setMovingLeft(false);
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) world.setMovingRight(false);
            }
        });
        try {
            SpriteAtlas atlas = new SpriteAtlas(
                    "assets/egg-catcher.atlas",
                    "assets/egg-catcher.png");
            BitmapFont font = new BitmapFont(
                    "assets/BoxyBold.fnt",
                    "assets/BoxyBold.png",
                    GameWindow.SCALE);
            renderer = new Renderer(atlas, font, GameWindow.SCALE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load game assets — " +
                    "make sure the 'assets/' folder is next to your .jar or on the classpath.\n" + e.getMessage(), e);
        }
    }


    public void runLoop() {
        long lastTime = System.nanoTime();

        while (true) {
            long now  = System.nanoTime();
            float dt  = (now - lastTime) / 1_000_000_000f;
            lastTime  = now;

            if (dt > 0.05f) dt = 0.05f;

            world.update(dt);
            renderToBuffer();
            repaint();

            long elapsed = System.nanoTime() - now;
            long sleepNs = FRAME_NS - elapsed;
            if (sleepNs > 0) {
                try { Thread.sleep(sleepNs / 1_000_000L, (int)(sleepNs % 1_000_000L)); }
                catch (InterruptedException ignored) {}
            }
        }
    }

    private void renderToBuffer() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;

        if (backBuffer == null || backBuffer.getWidth(null) != w || backBuffer.getHeight(null) != h) {backBuffer = createImage(w, h);
            bbg = (Graphics2D) backBuffer.getGraphics();
        }

        bbg.setColor(Color.BLACK);
        bbg.fillRect(0, 0, w, h);

        if (renderer != null) renderer.render(bbg, world);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backBuffer != null) g.drawImage(backBuffer, 0, 0, null);
    }

    private void handleMouseClick(MouseEvent e) {
        World.Screen current = world.getScreen();
        int mouseY = e.getY() / GameWindow.SCALE;

        if (current == World.Screen.MENU) {
            if (mouseY > 160 && mouseY < 200) world.startGame();
            else if (mouseY >= 200 && mouseY < 240) world.setScreen(World.Screen.SETTINGS);
        } else if (current == World.Screen.GAME_OVER) {
            if (mouseY > 160 && mouseY < 200) world.startGame();
            else if (mouseY >= 200 && mouseY < 240) world.setScreen(World.Screen.MENU);
        } else if (current == World.Screen.SETTINGS) {
            if (mouseY > 100 && mouseY < 140) GameConfig.fallSpeedMultiplier = 0.6f;
            else if (mouseY >= 140 && mouseY < 180) GameConfig.fallSpeedMultiplier = 1.0f;
            else if (mouseY >= 180 && mouseY < 220) GameConfig.fallSpeedMultiplier = 1.4f;
            else if (mouseY >= 240 && mouseY < 280) world.setScreen(World.Screen.MENU);
        }
    }


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}