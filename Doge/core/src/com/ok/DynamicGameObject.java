package com.ok;

import com.badlogic.gdx.math.Vector2;

public abstract class DynamicGameObject extends GameObject {

    public DynamicGameObject (float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public abstract void finish();
}
