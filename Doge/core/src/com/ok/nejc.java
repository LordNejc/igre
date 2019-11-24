package com.ok;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ok.debug.DebugCameraController;
import com.ok.debug.DebugViewportUtils;
import com.ok.debug.MomoryInfo;


/**
 * Artwork from https://goodstuffnononsense.com/about/
 * https://goodstuffnononsense.com/hand-drawn-icons/space-icons/
 */
public class nejc extends ApplicationAdapter {
	private Texture playerImg;

	private SpriteBatch batch;
	private OrthographicCamera camera;

	public Doge player;

	private List<Ball> balls; //special LibGDX Array
	private Array<Coco> cocos;
	public List<DynamicGameObject> objects;

	private long lastBallTime;
	private long lastCocoTime;

	private BitmapFont font;

	//Values are set experimental
	private static long CREATE_OBJECT_TIME = 1000000000; //ns
	MomoryInfo momoryInfo;

	//debug
	DebugCameraController dcc;
	ShapeRenderer sr;
	public Viewport vp;
	private boolean debug = false;

	public static Texture backgroundTexture;
	public static Sprite backgroundSprite;

	private void commandExitGame() {
		Gdx.app.exit();
	}
	private void commandReset() {
		create();
	}



	@Override
	public void create() {

		font = new BitmapFont();
		font.getData().setScale(2);

		// default way to load texture
		playerImg = new Texture(Gdx.files.internal("doge64.png"));
		backgroundTexture = new Texture("grass.jpg");
		backgroundSprite =new Sprite(backgroundTexture);

		// create the camera and the SpriteBatch
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();

		//=============================================================================================//
		//=============================================================================================//
		//=============================================================================================//
		//Debug
		momoryInfo = new MomoryInfo(500);
		dcc = new DebugCameraController();
		dcc.setStartPosition((float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2);
		vp = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
		sr = new ShapeRenderer();

		// Player
		float dogex = Gdx.graphics.getWidth() / 2 - playerImg.getWidth() / 2; // center the player horizontally
		float dogey = 20; // bottom left corner of the player is 20 pixels above the bottom screen edge
		player = new Doge(dogex, dogey);
		player.ballsRescuedScore = 0;
		player.playerHealth = 3;

		objects = new ArrayList<DynamicGameObject>();
		//add first astronoutn and asteroid
		spawnObjects();

	}

	private  void spawnObjects(){
		spawnCoco();
		spawnBall();
	}
	private void spawnBall() {
		objects.add(Ball.poolBalls.obtain());
		lastBallTime = TimeUtils.nanoTime();
	}
	private void spawnCoco() {
		objects.add(Coco.poolCocos.obtain());
		lastBallTime = TimeUtils.nanoTime();
	}


	@Override
	public void render() { //runs every frame
		//clear screen
		Gdx.gl.glClearColor(0, 0, 0f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();
		batch.begin();
		{
			backgroundSprite.draw(batch);
		}
		batch.end();

		if (debug) {
			momoryInfo.update();
			dcc.handleDebugInput(Gdx.graphics.getDeltaTime());
			dcc.applyTo(camera);
			batch.begin();
			{
				GlyphLayout layout = new GlyphLayout(font, "FPS:"+Gdx.graphics.getFramesPerSecond());
				font.setColor(Color.YELLOW);
				font.draw(batch,layout,Gdx.graphics.getWidth()-layout.width, Gdx.graphics.getHeight()-50);

				font.setColor(Color.YELLOW);
				font.draw(batch, "RC:" + batch.totalRenderCalls, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() - 20);
				momoryInfo.render(batch,font);
			}
			batch.end();

			batch.totalRenderCalls = 0;

			DebugViewportUtils.drawGrid(vp, sr, 50);

			//Print rectangles
			sr.setProjectionMatrix(camera.combined);
			//https://libgdx.badlogicgames.com/ci/nightlies/docs/api/com/badlogic/gdx/graphics/glutils/ShapeRenderer.html
			sr.begin(ShapeRenderer.ShapeType.Line);
			{
				sr.setColor(1, 1, 0, 1);
				for (DynamicGameObject object : objects) {
					sr.rect(object.position.x, object.position.y, object.img.getWidth(), object.img.getHeight());
				}
				sr.rect(player.position.x, player.position.y,player.img.getWidth(),player.img.getHeight());
			}
			sr.end();
		}

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);
		// begin a new batch and draw the player, balls, cocos
		batch.begin();
		{ //add brackets just for intent

			batch.draw(playerImg, player.position.x, player.position.y);
			for (DynamicGameObject object : objects) {
				batch.draw(object.img, object.position.x, object.position.y);
			}

			font.setColor(Color.YELLOW);
			font.draw(batch, "" + player.ballsRescuedScore, Gdx.graphics.getWidth() - 50, Gdx.graphics.getHeight() - 20);
			font.setColor(Color.GREEN);
			font.draw(batch, "" + player.playerHealth, 20, Gdx.graphics.getHeight() - 20);

			//particle
			player.peR.setPosition(player.position.x+player.img.getWidth()/2, player.position.y+player.img.getHeight()/2);
			player.peR.update(Gdx.graphics.getDeltaTime()); //move line in update part
			player.peR.draw(batch);
			player.peL.setPosition(player.position.x+player.img.getWidth()/2, player.position.y+player.img.getHeight()/2);
			player.peL.update(Gdx.graphics.getDeltaTime()); //move line in update part
			player.peL.draw(batch);
		}
		batch.end();

		// process user input

		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) commandExitGame();
		if(Gdx.input.isKeyPressed(Input.Keys.R)) commandReset();

		if(Gdx.input.isKeyJustPressed(Keys.F5)) debug=!debug;

		// check if we need to create a new
		if(TimeUtils.nanoTime() - lastBallTime > CREATE_OBJECT_TIME) spawnObjects();

		if (player.playerHealth > 0) { //is game end?
			// move and remove any that are beneath the bottom edge of
			// the screen or that hit the player.
			for (Iterator<DynamicGameObject> iter = objects.iterator(); iter.hasNext(); ) {
				DynamicGameObject object = iter.next();
				object.update(Gdx.graphics.getDeltaTime());
				if (object.position.y + object.img.getHeight() < 0) {
					iter.remove();
					object.finish();
				}
				if (object.bounds.overlaps(player.bounds)) {
					object.overlap(player);
					iter.remove();
				}

			}

		} else { //health of player is 0 or less
			batch.begin();
			{
				font.setColor(Color.RED);
				font.draw(batch, "The END", Gdx.graphics.getHeight() / 2, Gdx.graphics.getHeight() / 2);
			}
			batch.end();
		}
		//if(player.playerHealth > 0) {
			player.update(camera);
		//}
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
		playerImg.dispose();
		batch.dispose();
		font.dispose();
	}
}
