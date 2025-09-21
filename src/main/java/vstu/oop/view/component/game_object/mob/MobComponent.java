package vstu.oop.view.component.game_object.mob;

import vstu.oop.model.entity.mob.Mob;
import vstu.oop.view.component.game_object.GameObjectComponent;

import javax.swing.*;

public class MobComponent<M extends Mob> extends GameObjectComponent<M> {
    public MobComponent(M model, ImageIcon sprite) {
        super(model, sprite);
    }
}
