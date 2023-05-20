package com.mygdx.game;

import static com.mygdx.game.HealthHaven.SCR_HEIGHT;
import static com.mygdx.game.HealthHaven.SCR_WIDTH;

import com.badlogic.gdx.math.MathUtils;

public class Fragment extends HavenObject {
    int typeFragment;
    int typeShip;
    float angle, speedRotation;

    public Fragment(float x, float y, float size, int type) {
        super(x, y, 0, 0);
        width = MathUtils.random(size/10, size/3);
        height = MathUtils.random(size/10, size/3);
        float a = MathUtils.random(0f, 360f);
        float v = MathUtils.random(2f, 5f);
        vx = v * MathUtils.sin(a);
        vy = v * MathUtils.cos(a);
        typeShip = type;
        typeFragment = MathUtils.random(0, 3);
        speedRotation = MathUtils.random(-5f, 5f);
    }

    @Override
    void move() {
        super.move();
        angle += speedRotation;
    }

    boolean outOfScreen() {
        return x < -width/2 || y < -height/2 || x > SCR_WIDTH+width/2 || y > SCR_HEIGHT+height/2;
    }
}
