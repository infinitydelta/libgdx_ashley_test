package com.mygdx.game.utility;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.mygdx.game.GameScreen;
import com.mygdx.game.MainGame;
import com.mygdx.game.components.*;

/**
 * Created by KS on 4/24/2015.
 */
public class Factory {

    //collision masks

    final static short PLAYER_COL = 0x1;
    final static short ENEMY_COL = 0x1 << 1;
    public final static short PLAYER_PROJ_COL = 0x1 << 2;
    final static short ENEMY_PROJ_COL = 0x1 << 3;
    public final static short WALL = 0x1 << 4;

    //textures
    static Texture kenny;
    static Texture bg_tile;
    public static Texture whiteball;
    public static Texture objects;
    public static Texture sandTiles;

    public static Animation runAnimation;
    public static Animation idleAnmation;





    public static void loadAssets()
    {
        //textures
        kenny = new Texture("p1_stand.png");
        bg_tile = new Texture("blacktile.png");
        whiteball = new Texture("white ball.png");
        objects = new Texture("objects.png");
        sandTiles = new Texture("map.png");

        //animation
        Texture walk = new Texture("minimalObjects_32x32Tiles.png");
        TextureRegion[][] temp = TextureRegion.split(walk, 32, 32); //rows = 4; num cols = 3
        TextureRegion[] walkFrames = new TextureRegion[6];
        TextureRegion[] idleFrames = new TextureRegion[6];

        int index = 0;
        for (int i = 0; i < 1; i++) // column length, number of rows
        {
            for (int j = 0; j < 6; j++) //row length, number of columns
            {
                if (index < 6)
                {
                    //System.out.println("index: " + index);
                    //System.out.println("i: " + i + "; j: " + j);
                    walkFrames[index] = temp[i][j];
                    idleFrames[index] = temp[1][j];
                    idleFrames[index].flip(true, false);
                    index++;
                }
            }
        }
        runAnimation = new Animation(1/6f, walkFrames);

        idleAnmation = new Animation(1/2f, idleFrames);

    }

    public static Entity createPlayer(int x, int y)
    {
        Entity player = GameScreen.pooledEngine.createEntity();
        PositionComponent p = new PositionComponent(x, y);
        player.add(p);

        //create a body for the player
        CircleShape circle = new CircleShape();
        circle.setRadius(.4f);
        short player_col = ENEMY_PROJ_COL | ENEMY_COL | WALL;
        CollisionComponent col = new CollisionComponent(GameScreen.world, BodyDef.BodyType.DynamicBody, circle, PLAYER_COL, player_col, p);

        player.add(new MovementComponent(col, GameScreen.world, 0, 0, 0));

        player.add(new VisualComponent(runAnimation));
        player.add(new PlayerComponent(player));
        GameScreen.pooledEngine.addEntity(player);

        return player;
    }

    public static Entity createWeapon()
    {

        Entity weapon = GameScreen.pooledEngine.createEntity();
        TextureRegion weap = new TextureRegion(objects, 3 * 32, 1 * 32, 32, 32);
        weapon.add(new PositionComponent(0, 0));
        weapon.add(new VisualComponent(weap));
        GameScreen.pooledEngine.addEntity(weapon);

        Entity e = GameScreen.pooledEngine.createEntity();
        e.add(new PositionComponent(0, 0));

        return weapon;

    }

    public static Entity createBullet(float x, float y, float angle, float vel)
    {
        Entity bullet = GameScreen.pooledEngine.createEntity();
        PositionComponent p = new PositionComponent(x, y, angle);
        bullet.add(p);
        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox(.2f, .1f);
        CircleShape circle = new CircleShape();
        circle.setRadius(.2f);

        float xVel = (float) Math.cos(angle) * vel;
        float yVel = (float) Math.sin(angle) * vel;
        short bullet_col = ENEMY_COL | WALL;
        CollisionComponent col = new CollisionComponent(GameScreen.world, BodyDef.BodyType.DynamicBody, circle, PLAYER_PROJ_COL, bullet_col, p);
        bullet.add(new MovementComponent(col, GameScreen.world, xVel, yVel, 0));
        //add visual
        //
        GameScreen.pooledEngine.addEntity(bullet);
        return bullet;
        //rectangle.dispose();
        //circle.dispose();
    }

    public static Entity createGround(float x, float y)
    {
        Entity e = GameScreen.pooledEngine.createEntity();
        PositionComponent p = new PositionComponent(x, y);
        e.add(p);
        TextureRegion t = new TextureRegion(Factory.sandTiles, 0, 0, 32, 32);
        e.add(new VisualComponent(t));
        GameScreen.pooledEngine.addEntity(e);
        return e;
    }

    public static Entity createFakeWall(float x, float y)
    {
        Entity e = GameScreen.pooledEngine.createEntity();
        PositionComponent p = new PositionComponent(x, y);
        e.add(p);
        TextureRegion t = new TextureRegion(Factory.sandTiles, 0, 1 * 32, 32, 32);
        e.add(new VisualComponent(t));
        GameScreen.pooledEngine.addEntity(e);
        return e;
    }

    public static Entity createWall(float x, float y)
    {
        Entity wall = GameScreen.pooledEngine.createEntity();
        PositionComponent p = new PositionComponent(x, y);
        wall.add(p);

        PolygonShape square = new PolygonShape();
        square.setAsBox(.5f, .5f);
        short all = PLAYER_COL | PLAYER_PROJ_COL | ENEMY_COL | ENEMY_PROJ_COL;
        CollisionComponent col = new CollisionComponent(GameScreen.world, BodyDef.BodyType.StaticBody, square, WALL, all, p);

        TextureRegion t = new TextureRegion(Factory.sandTiles, 0, 1 * 32, 32, 32);
        wall.add(new VisualComponent(t));
        GameScreen.pooledEngine.addEntity(wall);

        return wall;
    }



}
