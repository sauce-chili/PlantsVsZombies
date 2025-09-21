package vstu.oop.model.entity.mob.plant.impl.potato_mine;

import vstu.oop.model.entity.mob.attacking_strategy.PotatoMineAttack;
import vstu.oop.model.entity.mob.plant.AttackingPlantBehavior;

import java.util.concurrent.TimeUnit;

public class PotatoMineBehavior extends AttackingPlantBehavior<PotatoMine> {

    private final long GROWTH_TIMEOUT = TimeUnit.SECONDS.toMillis(10);
    private final long EXPLOSION_STATE_TIMEOUT = TimeUnit.SECONDS.toMillis(2);
    private Long plantedTick;
    private Long explosionTick;

    public PotatoMineBehavior(PotatoMineAttack attackStrategy) {
        super(attackStrategy);
    }

    @Override
    protected void behave(long tick) {
        if (isGrowth()) {
            boolean wasAttack = getAttackStrategy().attack(tick);
            if (wasAttack) {
                setExplosionTick(tick);
            }
        }
        if (explosionTimeoutExceeded()) {
            getPlant().kill();
        }
    }

    void setPlantedTick(long plantedTick) {
        if (getPlantedTick() == null) {
            this.plantedTick = plantedTick;
        }
    }

    private Long getPlantedTick() {
        return plantedTick;
    }

    public boolean isGrowth() {
        if (wasExplosion()) {
            return false;
        }

        Long plantedTick = getPlantedTick();
        if (plantedTick == null) {
            return false;
        }
        return System.currentTimeMillis() - plantedTick >= GROWTH_TIMEOUT;
    }

    private void setExplosionTick(long tick) {
        explosionTick = tick;
    }

    private boolean explosionTimeoutExceeded() {
        if (explosionTick == null) {
            return false;
        }
        return System.currentTimeMillis() - explosionTick >= EXPLOSION_STATE_TIMEOUT;
    }

    public boolean wasExplosion() {
        return explosionTick != null;
    }
}
