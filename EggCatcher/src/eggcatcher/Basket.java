package eggcatcher;

public class Basket {

    public static final int   WIDTH       = 32;
    public static final int   HEIGHT      = 32;
    public static final float Y           = 264f;   
    private static final float MIN_CENTER = 46f;
    private static final float MAX_CENTER = 304f;
    
    // Add a speed constant (pixels per second)
    private static final float MOVE_SPEED = 250f; 

    private float centerX;

    public Basket() {
        centerX = 175f;  
    }

    // Replace setFromMouseWorld with this method
    public void move(float direction, float dt) {
        centerX += direction * MOVE_SPEED * dt;
        // Keep the basket within screen bounds
        centerX = Math.max(MIN_CENTER, Math.min(MAX_CENTER, centerX));
    }

    public float getCenterX() { return centerX; }
    public float getLeft()    { return centerX - WIDTH / 2f; }
    public float getTop()     { return Y; }
}