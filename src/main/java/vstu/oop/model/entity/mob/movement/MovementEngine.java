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

        if (isNull(getLastMoveTime())) {
            setLastMoveTime(currentTick);
            return false;
        }

        long delta = currentTick - getLastMoveTime();
        long moveDistance = (delta * movable.getSpeed()) / SPEED_COEFFICIENT;

        Position newPosition = movable.getPosition().move(
                movable.getMoveDirection(),
                (int) moveDistance
        );
        movable.moveTo(newPosition);

        setLastMoveTime(currentTick);

        return false;
    }

    private Long getLastMoveTime() {
        return lastMoveTime;
    }

    private void setLastMoveTime(Long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }
}
