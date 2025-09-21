package vstu.oop.model.entity.mob.plant.impl;

import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.attacking_strategy.PeaShooterAttack;
import vstu.oop.model.entity.mob.plant.AttackingPlantBehavior;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.utils.Constance;

public class PeaShooter extends Plant {
    public PeaShooter(
            Field field
    ) {
        super(
                Constance.getDefaultPlantHitboxParameters(),
                new AttackingPlantBehavior<>(new PeaShooterAttack(field)),
                120
        );
    }
}
