package vstu.oop.model.entity.mob.damage;

import vstu.oop.model.core.world.Direction;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.core.world.Position;

import java.util.Comparator;

public class PlantProjectile extends Projectile {

    public PlantProjectile(
            Position pos,
            Field field,
            Direction movementDirection,
            long speed,
            long amountDamage
    ) {
        super(pos, field, movementDirection, speed, amountDamage);
    }

    @Override
    protected boolean dealDamageOnHit() {
        return getField().getZombies()
                .filter(this::hasCollision)
                .min(Comparator.comparingDouble(
                        zombie -> this.getPosition().distance(zombie.getPosition())
                ))
                .map(zombie -> {
                    zombie.applyDamage(this);
                    return true;
                })
                .orElse(false);
    }
}
