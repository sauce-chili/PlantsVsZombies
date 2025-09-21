package vstu.oop.model.entity.mob.movement;

import vstu.oop.model.core.world.Direction;
import vstu.oop.model.core.world.Position;

public interface Movable {
    long getSpeed();

    Position getPosition();

    Direction getMoveDirection();

    void moveTo(Position pos);

    boolean isReachedTargetPosition();
}
