package sk.dudas.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.math.Rectangle;
import sk.dudas.game.gameobject.BunnyHead;
import sk.dudas.game.gameobject.Feather;
import sk.dudas.game.gameobject.GoldCoin;
import sk.dudas.game.gameobject.Rock;
import sk.dudas.game.screen.MenuScreen;

/**
 * Created by OLO on 13. 1. 2015
 */
public class WorldController extends InputAdapter {

    public static final String TAG = WorldController.class.getName();

    private Game game;
    //    public Sprite[] testSprites;
//    public int selectedSprite;
    public CameraHelper cameraHelper;
    public Level level;
    public int lives;
    public int score;

    // Rectangles for collision detection
    private Rectangle r1 = new Rectangle();
    private Rectangle r2 = new Rectangle();

    private float timeLeftGameOverDelay;

    public WorldController(Game game) {
        this.game = game;
        init();
    }

    private void init() {
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
//        initTestObjects();
        lives = Constants.LIVES_START;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    private void initLevel() {
        score = 0;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead);
    }

    private void backToMenu () {
        // switch to menu screen
        game.setScreen(new MenuScreen(game));
    }

//    private void initTestObjects() {
//        // Create new array for 5 sprites
//        testSprites = new Sprite[5];
//
//        // Create empty POT-sized Pixmap with 8 bit RGBA pixel data
////        int width = 32;
////        int height = 32;
//
////        Pixmap pixmap = createProceduralPixmap(width, height);
////
////        // Create a new texture from pixmap data
////        Texture texture = new Texture(pixmap);
//
//        Array<TextureRegion> regions = new Array<TextureRegion>();
//        regions.add(Assets.instance.bunny.head);
//        regions.add(Assets.instance.feather.feather);
//        regions.add(Assets.instance.goldCoin.goldCoin);
//
//        // Create new sprites using the just created texture
//        for (int i = 0; i < testSprites.length; i++) {
//
////            Sprite spr = new Sprite(texture);
//            Sprite spr = new Sprite(regions.random());
//
//            // Define sprite size to be 1m x 1m in game world
//            spr.setSize(1, 1);
//
//            // Set origin to sprite's center
//            spr.setOrigin(spr.getWidth() / 2.0f, spr.getHeight() / 2.0f);
//
//            // Calculate random position for sprite
//            float randomX = MathUtils.random(-2.0f, 2.0f);
//            float randomY = MathUtils.random(-2.0f, 2.0f);
//            spr.setPosition(randomX, randomY);
//
//            // Put new sprite into array
//            testSprites[i] = spr;
//        }
//
//        // Set first sprite as selected one
//        selectedSprite = 0;
//    }

//    private Pixmap createProceduralPixmap(int width, int height) {
//        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
//
//        // Fill square with red color at 50% opacity
//        pixmap.setColor(1, 0, 0, 0.5f);
//        pixmap.fill();
//
//        // Draw a yellow-colored X shape on square
//        pixmap.setColor(1, 1, 0, 1);
//        pixmap.drawLine(0, 0, width, height);
//        pixmap.drawLine(width, 0, 0, height);
//
//        // Draw a cyan-colored border around square
//        pixmap.setColor(0, 1, 1, 1);
//        pixmap.drawRectangle(0, 0, width, height);
//
//        return pixmap;
//    }

    public void update(float deltaTime) {
        handleDebugInput(deltaTime);
        if (isGameOver()) {
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0) {
                backToMenu();
            }
        } else {
            handleInputGame(deltaTime);
        }
//        updateTestObjects(deltaTime);
        level.update(deltaTime);

        testCollisions();
        cameraHelper.update(deltaTime);
        if (!isGameOver() && isPlayerInWater()) {
            lives--;
            if (isGameOver()) {
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            } else {
                initLevel();
            }
        }
    }

    private void handleInputGame(float deltaTime) {
        if (cameraHelper.hasTarget(level.bunnyHead)) {
            // Player Movement
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
            } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
            } else {
                // Execute auto-forward movement on non-desktop platform
                if (Gdx.app.getType() != Application.ApplicationType.Desktop) {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
            }

            // Bunny Jump
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                level.bunnyHead.setJumping(true);
            } else {
                level.bunnyHead.setJumping(false);
            }
        }
    }

    private void handleDebugInput(float deltaTime) {
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;

//        // Selected Sprite Controls
//        float sprMoveSpeed = 5 * deltaTime;
//
//        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
//            moveSelectedSprite(-sprMoveSpeed, 0);
//        }
//
//        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
//            moveSelectedSprite(sprMoveSpeed, 0);
//        }
//
//        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
//            moveSelectedSprite(0, sprMoveSpeed);
//        }
//
//        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
//            moveSelectedSprite(0, -sprMoveSpeed);
//        }

        // Camera Controls (move)
        if (!cameraHelper.hasTarget(level.bunnyHead)) {
            float camMoveSpeed = 5 * deltaTime;
            float camMoveSpeedAccelerationFactor = 5;
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                camMoveSpeed *= camMoveSpeedAccelerationFactor;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                moveCamera(-camMoveSpeed, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                moveCamera(camMoveSpeed, 0);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                moveCamera(0, camMoveSpeed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                moveCamera(0, -camMoveSpeed);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)) {
                cameraHelper.setPosition(0, 0);
            }
        }

        // Camera controls (zoom)
        float camZoomSpeed = 1 * deltaTime;
        float camZoomSpeedAccelerationFactor = 5;
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            camZoomSpeed *= camZoomSpeedAccelerationFactor;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.COMMA)) {
            cameraHelper.addZoom(camZoomSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PERIOD)) {
            cameraHelper.addZoom(-camZoomSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SLASH)) {
            cameraHelper.setZoom(1);
        }

    }

    private void moveCamera(float x, float y) {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }


//    private void moveSelectedSprite(float x, float y) {
//        testSprites[selectedSprite].translate(x, y);
//    }

//    private void updateTestObjects(float deltaTime) {
//        // Get current rotation from selected sprite
//        float rotation = testSprites[selectedSprite].getRotation();
//
//        // Rotate sprite by 90 degrees per second
//        rotation += 90 * deltaTime;
//
//        // Wrap around at 360 degrees
//        rotation %= 360;
//
//        // Set new rotation value to selected sprite
//        testSprites[selectedSprite].setRotation(rotation);
//    }

    @Override
    public boolean keyUp(int keycode) {

        // Reset game world
        if (keycode == Input.Keys.R) {
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        }

//        // Select next sprite
//        else if (keycode == Input.Keys.SPACE) {
//            selectedSprite = (selectedSprite + 1) % testSprites.length;
//
//            // Update camera's target to follow the currently
//            // selected sprite
//            if (cameraHelper.hasTarget()) {
//                cameraHelper.setTarget(testSprites[selectedSprite]);
//            }
//
//            Gdx.app.debug(TAG, "Sprite #" + selectedSprite + " selected");
//        }
//
//        // Toggle camera follow
//        else if (keycode == Input.Keys.ENTER) {
//            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : testSprites[selectedSprite]);
//            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
//        }

        // Toggle camera follow
        else if (keycode == Input.Keys.ENTER) {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }

        // Back to Menu
        else if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            backToMenu();
        }

        return false;
    }

    private void onCollisionBunnyHeadWithRock(Rock rock) {
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f) {
            boolean hitLeftEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitLeftEdge) {
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            } else {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
            }
            return;
        }
        switch (bunnyHead.jumpState) {
            case GROUNDED:
                break;
            case FALLING:
            case JUMP_FALLING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                bunnyHead.jumpState = BunnyHead.JUMP_STATE.GROUNDED;
                break;
            case JUMP_RISING:
                bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
                break;
        }
    }

    private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin) {
        goldcoin.collected = true;
        score += goldcoin.getScore();
        Gdx.app.log(TAG, "Gold coin collected");
    }

    private void onCollisionBunnyWithFeather(Feather feather) {
        feather.collected = true;
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerup(true);
        Gdx.app.log(TAG, "Feather collected");
    }

    private void testCollisions() {
        r1.set(level.bunnyHead.position.x,
                level.bunnyHead.position.y,
                level.bunnyHead.bounds.width,
                level.bunnyHead.bounds.height);

        // Test collision: Bunny Head <-> Rocks
        for (Rock rock : level.rocks) {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyHeadWithRock(rock);

            // IMPORTANT: must do all collisions for valid
            // edge testing on rocks.
        }

        // Test collision: Bunny Head <-> Gold Coins
        for (GoldCoin goldcoin : level.goldcoins) {
            if (goldcoin.collected) continue;
            r2.set(goldcoin.position.x, goldcoin.position.y, goldcoin.bounds.width, goldcoin.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithGoldCoin(goldcoin);
            break;
        }

        // Test collision: Bunny Head <-> Feathers
        for (Feather feather : level.feathers) {
            if (feather.collected) continue;
            r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
            if (!r1.overlaps(r2)) continue;
            onCollisionBunnyWithFeather(feather);
            break;
        }
    }

    public boolean isGameOver () {
        return lives < 0;
    }

    public boolean isPlayerInWater () {
        return level.bunnyHead.position.y < -5;
    }
}
