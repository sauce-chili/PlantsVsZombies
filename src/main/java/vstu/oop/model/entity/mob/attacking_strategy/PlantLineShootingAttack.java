package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Direction;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.damage.PlantProjectile;
import vstu.oop.model.entity.mob.damage.Projectile;
import vstu.oop.model.entity.mob.zombie.Zombie;

import java.util.function.BiFunction;

public abstract class PlantLineShootingAttack extends LineShootingAttack<Zombie> {

    public PlantLineShootingAttack(Field field, long attackTimeout, int shootDistance) {
        super(field, attackTimeout, Zombie.class, Direction.EAST, shootDistance);
    }

    @Override
    protected abstract PlantProjectile buildDamage(Mob attackedMob);
}
