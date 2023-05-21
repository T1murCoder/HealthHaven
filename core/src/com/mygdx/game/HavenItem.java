package com.mygdx.game;


import static com.mygdx.game.HealthHaven.SCR_HEIGHT;
import static com.mygdx.game.HealthHaven.SCR_WIDTH;

import com.badlogic.gdx.math.MathUtils;

public class HavenItem extends HavenObject {
    float angle, speedRotation;
    TypeItem type;

    public HavenItem(float width, float height, TypeItem type) {
        super(0, 0, width, height);
        x = MathUtils.random(width/2, SCR_WIDTH-width/2);
        y = MathUtils.random(SCR_HEIGHT+height/2, SCR_HEIGHT*2);
        vy = MathUtils.random(-5f, -3f);
        speedRotation = MathUtils.random(-5f, 5f);
        this.type = type;
    }

    @Override
    void move() {
        super.move();
        angle += speedRotation;
    }


    boolean outOfScreen() {
        return y < -height/2;
    }
}
