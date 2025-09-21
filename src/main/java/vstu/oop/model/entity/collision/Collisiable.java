package vstu.oop.model.entity.collision;

import vstu.oop.model.core.world.Position;

public abstract class Collisiable {

    private Hitbox hitbox;

    public Collisiable(Hitbox hitbox) {
        this.hitbox = hitbox;
    }

    public Collisiable(Position center, HitboxParameters parameters) {
        this.hitbox = new Hitbox(center, parameters);
    }

    public boolean hasCollision(Collisiable other) {
        return hitbox.intersects(other.hitbox);
    }

    protected void updateHitboxPosition(Position newPosition) {
        hitbox.moveCenterTo(newPosition);
    }

    protected void updateHitboxParameters(HitboxParameters newHitboxParameters) {
        hitbox = new Hitbox(hitbox.getCenter(), newHitboxParameters);
    }

    public Hitbox getHitbox() {
        return hitbox.clone();
    }
}
