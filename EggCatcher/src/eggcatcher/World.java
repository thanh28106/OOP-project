package eggcatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class World {

    public enum Screen { MENU, SETTINGS, PLAYING, GAME_OVER }

    private static final int   NUM_CHICKENS  = 5;
    private static final float INIT_FIRE_RATE = 1.75f;  // seconds between drops
    private static final int   SCORE_CAP_STEP = 50;

    private Screen screen = Screen.MENU;

    private final Basket              basket;
    private final List<Chicken>       chickens;
    private final List<FallingObject> objects;
    private final Random              rng;

    private int   score;
    private int   lives;
    private float fireTimer;
    private float fireRate;
    private int   scoreCap;
    
    private boolean movingLeft = false;
    private boolean movingRight = false;
    
    public interface Listener {
        void onGameOver(int finalScore);
    }
    private Listener listener;

    public World() {
        basket   = new Basket();
        chickens = new ArrayList<>(NUM_CHICKENS);
        objects  = new ArrayList<>();
        rng      = new Random();
        for (int i = 0; i < NUM_CHICKENS; i++) chickens.add(new Chicken(i));
    }

    public void setListener(Listener l) { this.listener = l; }
    public void setScreen(Screen screen) { this.screen = screen; }
    
    public void setMovingLeft(boolean moving) { this.movingLeft = moving; }
    public void setMovingRight(boolean moving) { this.movingRight = moving; }
    // api
    

    public void startGame() {
        score     = 0;
        lives     = 5;
        fireTimer = 0f;
        fireRate  = INIT_FIRE_RATE;
        scoreCap  = SCORE_CAP_STEP;
        objects.clear();
        for (Chicken ch : chickens) { ch.active = false; ch.timer = 0; ch.resetVal = 0.3f; }
        screen = Screen.PLAYING;
    }

    public void update(float dt) {
        if (screen != Screen.PLAYING) return;
        
        if (movingLeft) basket.move(-1f, dt);
        if (movingRight) basket.move(1f, dt);

        updateFireTimer(dt);
        updateChickens(dt);
        updateFallingObjects(dt);
        checkDifficulty();
    }

    public void update1(float dt) {
        if (screen != Screen.PLAYING) return;

        // Apply keyboard movement
        if (movingLeft) basket.move(-1f, dt);
        if (movingRight) basket.move(1f, dt);

        updateFireTimer(dt);
        updateChickens(dt);
        updateFallingObjects(dt);
        checkDifficulty();
    }
    
    public Screen         getScreen()    { return screen; }
    public int            getScore()     { return score; }
    public int            getLives()     { return lives; }
    public Basket         getBasket()    { return basket; }
    public List<Chicken>  getChickens()  { return chickens; }
    public List<FallingObject> getObjects() { return objects; }
    
    
    private void updateFireTimer(float dt) {
        fireTimer += dt;
        if (fireTimer >= fireRate) {
            fireTimer = 0f;
            int idx = rng.nextInt(NUM_CHICKENS);
            chickens.get(idx).fire();
            spawnObject(idx);
        }
    }

    private void spawnObject(int chickenIdx) {
        int r    = rng.nextInt(20);
        int type = (r == 19) ? 2 : (r % 2 == 0) ? 1 : 0;

        FallingObject obj = new FallingObject();
        obj.init(chickenIdx, type);
        objects.add(obj);
    }

    private void updateChickens(float dt) {
        for (Chicken ch : chickens) ch.update(dt);
    }

    private void updateFallingObjects(float dt) {
        for (int i = objects.size() - 1; i >= 0; i--) {
            FallingObject obj = objects.get(i);
            obj.update(dt, basket);

            if ((obj.caught || obj.missed) && obj.splatTimer == 0f) {
                applyResult(obj);
            }

            if (obj.isSplatDone()) {
                objects.remove(i);
            }
        }
    }

    private final java.util.Set<FallingObject> scored = new java.util.HashSet<>();

    private void applyResult(FallingObject obj) {
        if (scored.contains(obj)) return;
        scored.add(obj);

        if (obj.caught) {
            switch (obj.type) {
                case 0 -> {                          // dropping caught → -5, -life if 0
                    score = Math.max(0, score - 5);
                    if (score == 0) loseLife();
                }
                case 1 -> score += 5;               // white egg
                case 2 -> score += 10;              // golden egg
            }
        } else if (obj.missed) {
            if (obj.type != 0) loseLife();          // missed real egg → lose life
        }
    }

    private void loseLife() {
        lives--;
        if (lives <= 0) {
            lives  = 0;
            screen = Screen.GAME_OVER;
            if (listener != null) listener.onGameOver(score);
        }
    }

    private void checkDifficulty() {
        if (score >= scoreCap) {
            fireRate  = Math.max(0.4f, fireRate  * 0.8f);
            for (Chicken ch : chickens) ch.resetVal = Math.max(0.1f, ch.resetVal * 0.95f);
            scoreCap += SCORE_CAP_STEP;
        }
    }
}
