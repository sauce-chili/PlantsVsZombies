package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.plant.Plant;

public class ZombieMeleeAttack extends MeleeAttack<Plant>{
    public ZombieMeleeAttack(
            Field field,
            long attackTimeout,
            long damage
    ) {
        super(field, attackTimeout, Plant.class, damage);
    }
}
