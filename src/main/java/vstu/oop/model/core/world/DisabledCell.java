package vstu.oop.model.core.world;

import vstu.oop.model.entity.mob.plant.Plant;

import java.util.Optional;

public class DisabledCell extends Cell {

    public DisabledCell(Position leftTop, int width, int height) {
        super(leftTop, width, height);
    }

    @Override
    public Optional<Plant> getPlant() {
        return Optional.empty();
    }

    @Override
    public boolean canBePlanted(Plant plant) {
        return false;
    }

    @Override
    public Optional<Throwable> setPlant(Plant plant) {
        return Optional.of(new IllegalArgumentException("This cell can not hold a plant"));
    }

    @Override
    public boolean unsetPlant() {
        return false;
    }

    @Override
    public boolean planted() {
        return false;
    }

    @Override
    public String toString() {
        return "DisabledCell[leftTop=" + getLeftTop() + ", width=" + width() + ", height=" + height() + "]";
    }
}
