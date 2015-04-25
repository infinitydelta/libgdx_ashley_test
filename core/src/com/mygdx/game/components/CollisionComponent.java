package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;

/**
 * Created by KS on 3/12/2015.
 * creates a body/collider in box2d handled world, so no need to update it ourselves.
 * pass in type (static vs dynamic etc) and shape and will create a body in box2d
 */
public class CollisionComponent extends Component implements Pool.Poolable {


    //
    // NOT UPDATED BY OUR OWN SYSTEMS, ONLY BOX2D
    //

    Body body;
    Fixture fixture;

    public CollisionComponent(World world, BodyDef.BodyType type, Shape shape, PositionComponent position)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(position.x + .5f, position.y + .5f); //manual offset, will have to change; or just .5 of 1unit ie 32px

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        //PolygonShape rectangle = new PolygonShape();
        //rectangle.setAsBox(.5f, .5f);

        fixtureDef.shape = shape;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        fixture = body.createFixture(fixtureDef);

        shape.dispose();
    }

    public CollisionComponent(Fixture fixture, World world)
    {

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        //bodyDef.position.set(position.x + .5f, position.y + .5f);

        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox(.5f, .5f);

        fixtureDef.shape = rectangle;
        fixtureDef.density = 0f;
        fixtureDef.friction = 0f;
        fixtureDef.restitution = 0f;
        //Fixture fixture = body.createFixture(fixtureDef);
        rectangle.dispose();
    }

    @Override
    public void reset() {

    }
}
