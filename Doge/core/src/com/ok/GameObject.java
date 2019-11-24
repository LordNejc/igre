package com.ok;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    public final Vector2 position;
    public final Rectangle bounds;
    public Texture img;
    public double velocity;
    public Sound wof, dead;

    public GameObject (float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
        this.bounds = new Rectangle(x - width / 2, y - height / 2, width, height);
        wof = Gdx.audio.newSound(Gdx.files.internal("wof2.mp3"));
        //wof = Gdx.audio.newSound(Gdx.files.internal("coin.wav"));
        dead = Gdx.audio.newSound(Gdx.files.internal("Twirl2.wav"));
    }

    public void update(float deltaTime){
        position.y -= velocity * deltaTime;
        bounds.x = position.x - bounds.width / 2;
        bounds.y = position.y - bounds.height / 2;
    }

    public abstract void overlap(Doge player);
}
