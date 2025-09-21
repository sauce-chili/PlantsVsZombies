package vstu.oop.view.component;

import vstu.oop.model.entity.GameObject;
import vstu.oop.model.entity.mob.damage.Pea;
import vstu.oop.model.entity.mob.damage.Projectile;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;
import vstu.oop.model.entity.mob.zombie.Zombie;
import vstu.oop.view.component.game_object.mob.plant.PlantComponent;
import vstu.oop.view.component.game_object.mob.plant.PotatoMineComponent;
import vstu.oop.view.component.game_object.mob.zombie.ZombieComponent;
import vstu.oop.view.component.game_object.projectile.ProjectileComponent;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;

import static vstu.oop.utils.Util.UNCHECKED;
import static vstu.oop.utils.Util.loadByPath;

public class ComponentFactory {

    private final static Map<Class<? extends GameObject>, BufferedImage> sprites = Map.of(
            // Растения
            PeaShooter.class, loadByPath("src/main/resources/component/mob/plant/peashooter.png"),
            SunFlower.class, loadByPath("src/main/resources/component/mob/plant/sunflower.png"),
            // Зомби
            SimpleZombie.class, loadByPath("src/main/resources/component/mob/zombie/simple_zombie.png"),
            // Снаряды
            Pea.class, loadByPath("src/main/resources/component/projectile/pea.png")
    );

    @SuppressWarnings(UNCHECKED)
    public static <C extends Component<?>> C create(Object obj) {
        return (C) switch (obj) {
            case GameObject go -> createGameObject(go);
            default -> throw new IllegalArgumentException("Object " + obj + " not supported");
        };
    }

    public static Component<?> createGameObject(GameObject go) {
        var img = sprites.getOrDefault(go.getClass(), null);
        ImageIcon icon = img != null ? new ImageIcon(img) : null;
        return switch (go) {
            case PotatoMine mine -> new PotatoMineComponent(mine);
            case Plant plant -> new PlantComponent<>(plant, icon);
            case Zombie zombie -> new ZombieComponent<>(zombie, icon);
            case Projectile projectile -> new ProjectileComponent<>(projectile, icon);
            default -> throw new IllegalArgumentException("Object " + go + " not supported");
        };
    }
}
