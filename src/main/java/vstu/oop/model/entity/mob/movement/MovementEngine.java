package vstu.oop.model.entity.mob.movement;

import vstu.oop.model.core.world.Position;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

public class MovementEngine {

    private final static long SPEED_COEFFICIENT = 250;
    private Long lastMoveTime;

    private final Movable movable;

    public MovementEngine(Movable movable) {
        requireNonNull(movable);
        this.movable = movable;
    }

    public boolean move(long currentTick) {

        if (movable.isReachedTargetPosition()) {
            return true;
        }

        if (wasNoMovement()) {
            return false;
        }

        long moveDistance = calculateMoveDistance(tick);

        Position newPosition = movable.getPosition().move(
                movable.getMoveDirection(),
                (int) moveDistance
        );
        movable.moveTo(newPosition);

        setLastMoveTime(tick);

        return false;
    }

    private boolean wasNoMovement() {
        return isNull(getLastMoveTime());
    }

    private Long getLastMoveTime() {
        return lastMoveTime;
    }

    private long calculateMoveDistance(long tick) {
        long delta = tick - getLastMoveTime();
        long moveDistance = (delta * movable.getSpeed()) / SPEED_COEFFICIENT;
        return moveDistance;
    }

    private void setLastMoveTime(Long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }
}
