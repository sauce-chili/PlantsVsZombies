package vstu.oop.model.core.world;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public record Position(int x, int y) {
    public Position move(Direction direction, int shift) {
        return switch (direction) {
            case NORTH -> new Position(x, y - shift);
            case NORTH_EAST -> new Position(x + shift, y - shift);
            case EAST -> new Position(x + shift, y);
            case SOUTH_EAST -> new Position(x + shift, y + shift);
            case SOUTH -> new Position(x, y + shift);
            case SOUTH_WEST -> new Position(x - shift, y + shift);
            case WEST -> new Position(x - shift, y);
            case NORTH_WEST -> new Position(x - shift, y - shift);
        };
    }

    public double distance(Position pos) {
        return sqrt(pow(x - pos.x, 2) + pow(y - pos.y, 2));
    }
}