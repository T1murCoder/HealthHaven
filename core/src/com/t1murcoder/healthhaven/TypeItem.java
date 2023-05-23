package com.t1murcoder.healthhaven;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TypeItem {
    TextureRegion img;
    int impact;
    String name;

    public TypeItem(TextureRegion img, int impact, String name) {
        this.img = img;
        this.impact = impact;
        this.name = name;
    }
}
