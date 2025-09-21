package vstu.oop.model.entity.mob.plant.impl.potato_mine;

import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.attacking_strategy.MeleeAttack;
import vstu.oop.model.entity.mob.attacking_strategy.PotatoMineAttack;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.zombie.Zombie;
import vstu.oop.utils.Constance;

public class PotatoMine extends Plant {

    public PotatoMine(Field field) {
        super(
                Constance.getDefaultPlantHitboxParameters(),
                new PotatoMineBehavior(
                        new PotatoMineAttack(field)
                ),
                80
        );
    }

    @Override
    public void setCell(Cell cell) {
        super.setCell(cell);
        behavior().setPlantedTick(System.currentTimeMillis());
    }

    @Override
    public PotatoMineBehavior behavior() {
        return (PotatoMineBehavior) super.behavior();
    }
}
