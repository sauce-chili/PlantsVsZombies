package vstu.oop.model.entity.mob.zombie;

import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.attacking_strategy.ZombieMeleeAttack;

import static vstu.oop.utils.Constance.getDefaultZombieHitboxParameters;

public class SimpleZombie extends Zombie {
    public SimpleZombie(
            Position position,
            Field field
    ) {
        super(
                new Hitbox(position, getDefaultZombieHitboxParameters()),
                new ZombieMeleeAttack(
                        field,
                        100,
                        15
                ),
                field,
                100,
                15
        );
    }
}
