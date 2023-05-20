package com.mygdx.game;

public class HavenObject {
    float x, y;
    float width, height;
    float vx, vy;

    public HavenObject(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    void move() {
        x += vx;
        y += vy;
    }

    float getX() {
        return x-width/2;
    }

    float getY() {
        return y-height/2;
    }
}
