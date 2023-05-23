package com.t1murcoder.healthhaven;


import static com.t1murcoder.healthhaven.HealthHaven.SCR_WIDTH;

public class Guy extends HavenObject {
    boolean isAlive;
    int health;

    public Guy(float x, float y, float width, float height) {
        super(x, y, width, height);
        isAlive = true;
        health = 100;
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

    boolean overlap(HavenItem item) {
        return Math.abs(x-item.x) < width/2+item.width/2 & Math.abs(y-item.y) < height/2+item.height/2;
    }
}