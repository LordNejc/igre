package com.ok;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class Coco extends DynamicGameObject implements Pool.Poolable {
    public static float COCO_WIDTH = 100;
    public static float COCO_HEIGHT = 50;

    public static final Pool<Coco> poolCocos = Pools.get(Coco.class, 50);

    public Coco (){
        super(MathUtils.random(0, Gdx.graphics.getWidth()), Gdx.graphics.getHeight() ,COCO_WIDTH, COCO_HEIGHT );
        velocity = Math.random() * ( 1000 - 100 );
        img = new Texture(Gdx.files.internal("coco128.png"));
    }
    public void init(float posX, float posY, double speed) {
        position.set(posX,  posY);
        velocity = speed;
    }

    @Override public void reset() {
        init(MathUtils.random(0, Gdx.graphics.getWidth()), Gdx.graphics.getHeight(),Math.random() * ( 1000 - 100 )); //Called when it is returned to pool (free method is called)
    }
    public void finish() { poolCocos.free(this); }

    @Override
    public void overlap(Doge player) {
        finish();
        dead.play();
       player.playerHealth--;
    }
}
