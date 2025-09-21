package vstu.oop.view.component.game_object.mob.plant;

import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;

import javax.swing.*;
import java.awt.*;

import static vstu.oop.utils.Util.loadByPath;

public class PotatoMineComponent extends PlantComponent<PotatoMine> {

    private final static ImageIcon POTATO_MINE_SPROUT = new ImageIcon(loadByPath(
            "src/main/resources/component/mob/plant/potato_mine_sprout.png"
    ));
    private final static ImageIcon POTATO_MINE = new ImageIcon(loadByPath(
            "src/main/resources/component/mob/plant/potato_mine.png"
    ));
    private final static ImageIcon BOOM_SPRITE = new ImageIcon(loadByPath(
            "src/main/resources/component/mob/plant/potato_mine_boom.png"
    ));

    public PotatoMineComponent(
            PotatoMine plant
    ) {
        super(plant, POTATO_MINE_SPROUT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        boolean isGrowth = getModel().behavior().isGrowth();
        boolean wasExplosion = getModel().behavior().wasExplosion();
        if (isGrowth) {
            setIfNewSprite(POTATO_MINE);
        } else if (wasExplosion) {
            setIfNewSprite(BOOM_SPRITE);
        }
        super.paintComponent(g);
    }
}
