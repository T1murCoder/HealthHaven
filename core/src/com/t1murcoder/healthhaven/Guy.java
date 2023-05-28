package com.t1murcoder.healthhaven;


import static com.t1murcoder.healthhaven.HealthHaven.SCR_WIDTH;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;

public class Guy extends HavenObject {
    boolean isAlive;
    int health;
    int phase, nPhases = 9;
    long timeLastPhase, timePhaseInterval = 100;

    public Guy(float x, float y, float width, float height) {
        super(x, y, width, height);
        isAlive = true;
        health = 100;
    }

    @Override
    void move() {
        super.move();
        outOfScreen();
        changePhase();
    }

    void changePhase(){
        if(timeLastPhase+timePhaseInterval < TimeUtils.millis()) {
            if (++phase == nPhases) phase = 0;
            timeLastPhase = TimeUtils.millis();
        }
        if(Math.abs(vx) < 0.1) phase = 6;
    }

    int flip(){
        return vx<0?-1:1;
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
        return Math.abs(x-item.x) < width/10+item.width/10 & Math.abs(y-item.y) < height/10+item.height/2;
    }
}
