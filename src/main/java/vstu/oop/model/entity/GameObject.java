package vstu.oop.model.entity;

import vstu.oop.model.entity.collision.Collisiable;
import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.core.world.Position;

public abstract class GameObject extends Collisiable {

    public GameObject(Hitbox hitbox) {
        super(hitbox);
    }

    public abstract void act(long currentTick);
    
    public Position getPosition() {
        return getHitbox().getCenter();
    }

    protected void setPosition(Position pos) {
        updateHitboxPosition(pos);
    }
}
