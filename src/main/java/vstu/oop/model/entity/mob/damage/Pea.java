package vstu.oop.model.entity.mob.damage;

import vstu.oop.model.core.world.Direction;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.core.world.Position;

public class Pea extends PlantProjectile {
    public Pea(
            Position pos,
            Field field,
            Direction movementDirection
    ) {
        super(pos, field, movementDirection, 50, 20);
    }
}
