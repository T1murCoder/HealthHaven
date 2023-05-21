package com.mygdx.game;


import static com.mygdx.game.HealthHaven.SCR_HEIGHT;

public class ShipShot extends HavenObject {
    public ShipShot(float x, float y, float width, float height) {
        super(x, y, width, height);
        vy = 8;
    }

    boolean outOfScreen() {
        return y > SCR_HEIGHT+height/2;
    }

    boolean overlap(HavenItem e) {
        return Math.abs(x-e.x) < width/2+e.width/2 & Math.abs(y-e.y) < height/2+e.height/2;
    }
}
