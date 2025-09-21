package vstu.oop.model.core.world;

import vstu.oop.model.entity.mob.plant.Plant;

import java.util.Optional;

import static java.util.Objects.*;

public class Cell {

    private final Position leftTop;
    private final int width;
    private final int height;

    private Plant plant;

    public Cell(Position leftTop, int width, int height) {
        requireNonNull(leftTop, "The leftTop position cannot be null");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("The width and height must be greater than 0");
        }
        this.leftTop = leftTop;
        this.width = width;
        this.height = height;
    }

    public Optional<Plant> getPlant() {
        return Optional.ofNullable(plant);
    }

    public boolean canBePlanted(Plant plant) {
        return isNull(this.plant) && nonNull(plant) && isNull(plant.getCell());
    }

    public boolean canNotBePlanted(Plant plant) {
        return !canBePlanted(plant);
    }

    public Optional<Throwable> setPlant(Plant plant) {
        if (canNotBePlanted(plant)) {
            return Optional.of(new IllegalArgumentException("This cell can't be planted this plant"));
        }
        this.plant = plant;
        plant.setCell(this);

        return Optional.empty();
    }

    public boolean unsetPlant() {
        if (isNull(plant)) {
            return false;
        }
        Plant tmp = plant;
        plant = null;
        tmp.digUp();
        return true;
    }

    public boolean planted() {
        return plant != null;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public Position getLeftTop() {
        return leftTop;
    }

    public Position getRightTop() {
        return new Position(leftTop.x() + width, leftTop.y());
    }

    public Position getLeftBottom() {
        return new Position(leftTop.x(), leftTop.y() + height);
    }

    public Position getRightBottom() {
        return new Position(leftTop.x() + width, leftTop.y() + height);
    }

    public Position getCenter() {
        return new Position(leftTop.x() + width / 2, leftTop.y() + height / 2);
    }

    public boolean positionInCell(Position pos) {
        int x = pos.x();
        int y = pos.y();
        return x >= leftTop.x() && x < leftTop.x() + width &&
                y >= leftTop.y() && y < leftTop.y() + height;
    }

    @Override
    public String toString() {
        return "Cell[leftTop=" + leftTop + ", width=" + width + ", height=" + height + "]";
    }
}
