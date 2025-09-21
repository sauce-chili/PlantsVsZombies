package vstu.oop.model.entity.mob.plant;

import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.entity.collision.HitboxParameters;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.entity.mob.damage.Damage;
import vstu.oop.model.entity.mob.Mob;

import static java.util.Objects.*;

public abstract class Plant extends Mob {

    protected Cell cell;
    protected PlantBehaviorStrategy behavior;

    public Plant(HitboxParameters hitboxParameters, PlantBehaviorStrategy behavior, long health) {
        super(new Hitbox(Integer.MIN_VALUE, Integer.MIN_VALUE, hitboxParameters), health);
        this.behavior = behavior;
        this.behavior.setPlant(this);
    }

    @Override
    public final void act(long currentTick) {
        if (isNull(getCell())) {
            throw new IllegalStateException("Plant cannot be used until it is planted");
        }
        if (isAlive()) {
            behavior().apply(currentTick);
        }
    }

    @Override
    public void applyDamage(Damage damage) {
        // Plants do not take damage from other plants
        if (damage.emitter() instanceof Plant) {
            return;
        }
        super.applyDamage(damage);
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        if (nonNull(getCell())) {
            throw new IllegalArgumentException("The plant cannot be planted again.");
        }
        Plant cellPlant = cell.getPlant().orElseThrow(() -> new IllegalStateException(
                "At the time of planting the plant, the cell should already contain the same plant."
        ));
        if (cellPlant != this) {
            throw new IllegalArgumentException(
                    "At the time of planting the plant, the cell should already contain the same plant."
            );
        }
        this.cell = cell;
        this.setPosition(cell.getCenter());
    }

    public void digUp() {
        if (isNull(getCell())) return;
        getCell().getPlant()
                .filter(p -> p == this)
                .ifPresent(ignored -> {
                    throw new IllegalStateException("The plant cannot be dig-up until it is planted in cell");
                });
        this.cell = null;
        this.setPosition(new Position(Integer.MIN_VALUE, Integer.MIN_VALUE));
    }

    public PlantBehaviorStrategy<?> behavior() {
        return behavior;
    }
}
