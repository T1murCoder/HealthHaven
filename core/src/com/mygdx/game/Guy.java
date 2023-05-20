package com.mygdx.game;


import static com.mygdx.game.HealthHaven.SCR_WIDTH;

public class Guy extends HavenObject {
    boolean isAlive;
    int lives;

    public Guy(float x, float y, float width, float height) {
        super(x, y, width, height);
        isAlive = true;
        lives = 1;
    }

    @Override
    void move() {
        super.move();
        outOfScreen();
    }

    void outOfScreen() {
        if(x<0+width/2) {
            x = 0+width/2;
            vx = 0;
        }
        if(x>SCR_WIDTH-width/2) {
            x = SCR_WIDTH-width/2;
            vx = 0;
        }
    }
}
