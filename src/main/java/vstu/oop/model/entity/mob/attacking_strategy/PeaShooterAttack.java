package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Field;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.damage.Pea;

import java.util.concurrent.TimeUnit;

public class PeaShooterAttack extends PlantLineShootingAttack {
    public PeaShooterAttack(
            Field field
    ) {
        super(field, TimeUnit.SECONDS.toMillis(1), 6);
    }

    @Override
    protected Pea buildDamage(Mob attackedMob) {
        Position spawnPosition = getAttacker().getPosition().move(
                getAttackingDirection(),
                getAttacker().getHitbox().getHitboxParameters().width() / 2 + 10
        );
        Pea pea = new Pea(
                spawnPosition,
                getField(),
                getAttackingDirection()
        );
        pea.setEmitter(getAttacker());
        return pea;
    }
}
