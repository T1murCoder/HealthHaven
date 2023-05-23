package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class HavenButton {
    float x, y;
    float width, height;
    String text;
    BitmapFont font;
    boolean isTextButton;
    Texture img;

    public HavenButton(BitmapFont font, String text, float x, float y) {
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        GlyphLayout gl = new GlyphLayout(font, text);
        width = gl.width;
        height = gl.height;
        isTextButton = true;
    }

    public HavenButton(Texture img, float x, float y, float width, float height){
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        isTextButton = false;
    }

    boolean hit(float tx, float ty){
        if(isTextButton)
            return x < tx && tx < x + width && y > ty && ty > y - height;
        else
            return x < tx && tx < x + width && y < ty && ty < y + height;
    }

    public void setText(String text) {
        this.text = text;
        GlyphLayout gl = new GlyphLayout(font, text);
        width = gl.width;
    }
}
