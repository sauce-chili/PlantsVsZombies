package vstu.oop.model.entity.mob.plant.impl;

import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.ProducingPlantBehavior;
import vstu.oop.utils.Constance;

public class SunFlower extends Plant {
    public SunFlower(ProducingPlantBehavior.TokenSunConsumer sunConsumer) {
        super(
                Constance.getDefaultPlantHitboxParameters(),
                new ProducingPlantBehavior(30, 100L, sunConsumer),
                100
        );
    }
}
