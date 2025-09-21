package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;
import vstu.oop.model.entity.mob.zombie.Zombie;

import java.util.Set;
import java.util.stream.Collectors;

import static vstu.oop.utils.Constance.CELL_WIDTH;

public class PotatoMineAttack extends MeleeAttack<Zombie> {
    public PotatoMineAttack(Field field) {
        super(field, 0, Zombie.class, 120);
    }

    @Override
    public void setAttacker(Mob attacker) {
        if (attacker.getClass() != PotatoMine.class) {
            throw new IllegalArgumentException("This strategy only supports PotatoMine.");
        }
        super.setAttacker(attacker);
    }

    @Override
    protected Set<Zombie> getAttackedEnemies() {
        return getField().getZombieSnapshot().stream()
                .filter(z -> getAttacker().getPosition().distance(z.getPosition()) <= CELL_WIDTH)
                .collect(Collectors.toSet());
    }

    @Override
    protected PotatoMine getAttacker() {
        return (PotatoMine) super.getAttacker();
    }
}
