package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Direction;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.damage.Projectile;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public abstract class LineShootingAttack<E extends Mob> extends DistanceAttack<E> {

    public LineShootingAttack(
            Field field,
            long attackTimeout,
            Class<E> enemyType,
            Direction attackingDirection,
            int shootCellDistance
    ) {
        super(field, attackTimeout, enemyType, attackingDirection, shootCellDistance);
    }

    @Override
    protected void attackEnemies(Collection<? extends Mob> es) {
        getField().getProjectilesContainer()
                .add(buildDamage(es.stream().findFirst().orElseThrow()));
    }

    @Override
    protected abstract Projectile buildDamage(Mob attackedMob);
}
