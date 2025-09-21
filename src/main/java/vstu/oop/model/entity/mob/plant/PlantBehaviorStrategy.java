package vstu.oop.model.entity.mob.plant;

import static java.util.Objects.requireNonNull;

public abstract class PlantBehaviorStrategy<P extends Plant> {

    private P plant;

    final void apply(long tick) {
        requireNonNull(getPlant(), "Plant must be present");
        behave(tick);
    }

    abstract protected void behave(long tick);

    protected void setPlant(P plant) {
        requireNonNull(plant, "Plant must be present");
        if (getPlant() != null) {
            throw new IllegalStateException("Plant can be set only once");
        }
        this.plant = plant;
    }

    protected P getPlant() {
        return this.plant;
    }
}
