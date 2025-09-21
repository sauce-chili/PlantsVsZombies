package vstu.oop.view.component.game_object.mob.plant;

import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.view.component.game_object.mob.MobComponent;

import javax.swing.*;

import static vstu.oop.utils.Util.loadByPath;

public class PlantComponent<P extends Plant> extends MobComponent<P> {
    public PlantComponent(P plant, ImageIcon sprite) {
        super(plant, sprite);
    }
}
