package vstu.oop.view.component.game_object;

import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.GameObject;
import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.entity.collision.HitboxParameters;
import vstu.oop.view.component.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

public abstract class GameObjectComponent<Obj extends GameObject> extends Component<Obj> {

    private ImageIcon sprite;
    private final static boolean drawingHitBox = true;

    protected GameObjectComponent(Obj model, ImageIcon sprite) {
        super(model);
        Hitbox hitbox = getModel().getHitbox();
        Position topLeft = hitbox.getTopLeft();
        HitboxParameters hp = hitbox.getHitboxParameters();
        setPreferredSize(new Dimension(hp.width(), hp.height()));
        setBounds(
                topLeft.x(), topLeft.y(),
                hp.width(), hp.height()
        );
        setOpaque(false);

        setSprite(sprite);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        sprite.paintIcon(this, g, 0, 0);
        if (drawingHitBox) {
            drawHitBox(g);
        }
    }

    protected void drawHitBox(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(1));

            Hitbox hitbox = getModel().getHitbox();
            Point2D.Double[] vertices = hitbox.getVertices();

            Position topLeft = hitbox.getTopLeft();
            int[] xPoints = new int[vertices.length];
            int[] yPoints = new int[vertices.length];

            for (int i = 0; i < vertices.length; i++) {
                xPoints[i] = (int) (vertices[i].x - topLeft.x());
                yPoints[i] = (int) (vertices[i].y - topLeft.y());
            }

            g2d.drawPolygon(xPoints, yPoints, vertices.length);
        } finally {
            g2d.dispose();
        }
    }

    protected ImageIcon getSprite() {
        return sprite;
    }

    protected void setSprite(ImageIcon sprite) {
        this.sprite = sprite;
    }

    protected void setIfNewSprite(ImageIcon newSprite) {
        if (getSprite() != newSprite) {
            setSprite(newSprite);
        }
    }
}
