package eggcatcher;

import javax.swing.*;
import java.awt.*;

public class GameWindow extends JFrame {

    public static final int SCALE = 2;          // pixel scale factor
    public static final int WORLD_W = 352;       // world width
    public static final int WORLD_H = 320;       //  world height

    public GameWindow() {
        setTitle("Egg Catcher");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);

        Thread gameThread = new Thread(panel::runLoop, "GameLoop");
        gameThread.setDaemon(true);
        gameThread.start();
    }
}
