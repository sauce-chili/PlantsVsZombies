package vstu.oop.view.component.game_object.projectile;

import vstu.oop.model.entity.mob.damage.Projectile;
import vstu.oop.view.component.game_object.GameObjectComponent;

import javax.swing.*;

public class ProjectileComponent<P extends Projectile> extends GameObjectComponent<P> {
    public ProjectileComponent(P projectile, ImageIcon sprite) {
        super(projectile, sprite);
    }
}
