package eggcatcher;


public class Chicken {

    public static final int   WIDTH    = 32;
    public static final int   HEIGHT   = 32;
    public static final float Y        = 96f;  

    public final int   index;
    public final float x;

    public boolean active  = false;
    public float   timer   = 0f;
    public float   resetVal = 0.3f;   

    public Chicken(int index) {
        this.index = index;
        this.x     = 32f + index * 64f;
    }

    public void update(float dt) {
        if (active) {
            timer += dt;
            if (timer >= resetVal) {
                active = false;
                timer  = 0f;
            }
        }
    }

    public void fire() {
        active = true;
        timer  = 0f;
    }
}
