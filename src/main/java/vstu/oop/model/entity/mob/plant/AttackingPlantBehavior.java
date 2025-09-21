package vstu.oop.model.entity.mob.plant;

import vstu.oop.model.entity.mob.attacking_strategy.AttackStrategy;
import vstu.oop.model.entity.mob.zombie.Zombie;

public class AttackingPlantBehavior<P extends Plant> extends PlantBehaviorStrategy<P> {

    private AttackStrategy attackStrategy;

    public AttackingPlantBehavior(AttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
    }

    @Override
    protected void setPlant(P plant) {
        super.setPlant(plant);
        attackStrategy.setAttacker(plant);
    }

    @Override
    protected void behave(long tick) {
        attackStrategy.attack(tick);
    }

    protected AttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    protected void setAttackStrategy(AttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
    }
}
