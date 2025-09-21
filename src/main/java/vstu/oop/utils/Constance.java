package vstu.oop.utils;

import vstu.oop.model.core.world.FieldCharacteristics;
import vstu.oop.model.entity.collision.HitboxParameters;

public class Constance {

    // Размеры и позиционирование
    public final static int CELL_WIDTH = 80;
    public final static int CELL_HEIGHT = 100;
    public final static int FIELD_STARTING_X = 255;
    public final static int FIELD_STARTING_Y = 80;

    public final static int APP_WIDTH = 1100;
    public final static int FIELD_HEIGHT = 600;
    public final static int FIELD_WIDTH = APP_WIDTH;
    public final static int CATALOGUE_HEIGHT = 90;
    public final static int CATALOGUE_WIDTH = APP_WIDTH;
    public final static int APP_HEIGHT = FIELD_HEIGHT + CATALOGUE_HEIGHT;

    public final static FieldCharacteristics defaultFieldCharacteristics = new FieldCharacteristics(
            FIELD_WIDTH,
            FIELD_HEIGHT,
            5,
            9,
            CELL_WIDTH,
            CELL_HEIGHT,
            FIELD_STARTING_X,
            FIELD_STARTING_Y
    );

    public static HitboxParameters getDefaultPlantHitboxParameters() {
        return new HitboxParameters(CELL_WIDTH, CELL_HEIGHT, 0);
    }

    public static HitboxParameters getDefaultZombieHitboxParameters() {
        return new HitboxParameters(80, 100, 0);
    }
}
