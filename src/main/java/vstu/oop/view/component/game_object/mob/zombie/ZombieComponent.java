package vstu.oop.view.component.game_object.mob.zombie;

import vstu.oop.model.entity.mob.zombie.Zombie;
import vstu.oop.view.component.game_object.mob.MobComponent;

import javax.swing.*;

public class ZombieComponent<Z extends Zombie> extends MobComponent<Z> {
    public ZombieComponent(Z zombie, ImageIcon spite) {
        super(zombie, spite);
    }
}
