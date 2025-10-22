package vstu.oop.model.entity.mob.zombie;

import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.attacking_strategy.MeleeRandomNearestAttack;
import vstu.oop.model.entity.mob.attacking_strategy.RandomNearestAttack;
import vstu.oop.model.entity.mob.attacking_strategy.ZombieMeleeAttack;
import vstu.oop.model.entity.mob.plant.Plant;

import static vstu.oop.utils.Constance.getDefaultZombieHitboxParameters;

public class SimpleZombie extends Zombie {
    public SimpleZombie(
            Position position,
            Field field
    ) {
        super(
                new Hitbox(position, getDefaultZombieHitboxParameters()),
                new MeleeRandomNearestAttack<>(
                        field,
                        1000,
                        10,
                        Plant.class,
                        0.3
                ),
                field,
                100,
                15
        );
    }
}
