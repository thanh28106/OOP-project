package eggcatcher;

public class FallingObject {

    public static final float FALL_SPEED   = 160f;
    public static final float SPLAT_TTL    = 1.25f;

    private static final float CATCH_HALF  = 16f;
    static public float CATCH_Y     = 272f;  
    private static final float GROUND_Y    = 280f; 

    public float x, y;
    public int   type;

    public boolean falling      = true;
    public boolean caught       = false; 
    public boolean missed       = false;  
    public boolean splatting    = false;  
    public float   splatTimer   = 0f;

    private boolean alignedToBasket = false;

    public void init(int chickenIndex, int type) {
        this.type  = type;
        this.x     = 32f + chickenIndex * 64f;
        this.y     = 96f + 32f; 

        falling         = true;
        caught          = false;
        missed          = false;
        splatting       = false;
        splatTimer      = 0f;
        alignedToBasket = false;
    }

    public void update(float dt, Basket basket) {
        if (splatting) {
            splatTimer += dt;
            return;
        }
        if (!falling) return;

        y += FALL_SPEED * dt;

        if (y >= CATCH_Y - 20f && !alignedToBasket) {
            if (Math.abs(x - basket.getCenterX()) < CATCH_HALF + 8f) {
                alignedToBasket = true;
            }
        }

        if (alignedToBasket) {
            x = basket.getCenterX() - 16f;
        }

        if (y >= CATCH_Y && alignedToBasket) {
            falling   = false;
            caught    = true;
            splatting = true;
            y         = CATCH_Y;
            return;
        }

        if (y >= GROUND_Y && !alignedToBasket) {
            falling   = false;
            missed    = true;
            splatting = true;
            y         = GROUND_Y;
        }
    }

    public boolean isSplatDone() {
        return splatting && splatTimer >= SPLAT_TTL;
    }

    public boolean isActive() {
        return !isSplatDone();
    }
}
