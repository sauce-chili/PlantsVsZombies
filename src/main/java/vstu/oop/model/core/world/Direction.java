package vstu.oop.model.core.world;

public enum Direction {
    NORTH,
    NORTH_WEST,
    WEST,
    SOUTH_WEST,
    SOUTH,
    SOUTH_EAST,
    EAST,
    NORTH_EAST;

    public Direction opposite() {
        return switch (this) {
            case NORTH -> SOUTH;
            case NORTH_WEST -> SOUTH_EAST;
            case WEST -> EAST;
            case SOUTH_WEST -> NORTH_EAST;
            case SOUTH -> NORTH;
            case SOUTH_EAST -> NORTH_WEST;
            case EAST -> WEST;
            case NORTH_EAST -> SOUTH_WEST;
        };
    }

    public Direction nextByClockwise() {
        return switch (this) {
            case NORTH -> NORTH_EAST;
            case NORTH_EAST -> EAST;
            case EAST -> SOUTH_EAST;
            case SOUTH_EAST -> SOUTH;
            case SOUTH -> SOUTH_WEST;
            case SOUTH_WEST -> WEST;
            case WEST -> NORTH_WEST;
            case NORTH_WEST -> NORTH;
        };
    }

    public Direction nextByCounterClockwise() {
        return switch (this) {
            case NORTH -> NORTH_WEST;
            case NORTH_WEST -> WEST;
            case WEST -> SOUTH_WEST;
            case SOUTH_WEST -> SOUTH;
            case SOUTH -> SOUTH_EAST;
            case SOUTH_EAST -> EAST;
            case EAST -> NORTH_EAST;
            case NORTH_EAST -> NORTH;
        };
    }

    public double radian(){
        return toRadians(this);
    }

    public static double toRadians(Direction direction) {
        return switch (direction) {
            case NORTH -> -Math.PI / 2;
            case NORTH_EAST -> -Math.PI / 4;
            case EAST -> 0;
            case SOUTH_EAST -> Math.PI / 4;
            case SOUTH -> Math.PI / 2;
            case SOUTH_WEST -> 3 * Math.PI / 4;
            case WEST -> Math.PI;
            case NORTH_WEST -> -3 * Math.PI / 4;
        };
    }
}