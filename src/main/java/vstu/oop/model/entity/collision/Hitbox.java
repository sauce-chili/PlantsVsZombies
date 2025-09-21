package vstu.oop.model.entity.collision;

import vstu.oop.model.core.world.Position;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class Hitbox implements Cloneable {
    private int x;
    private int y;
    private HitboxParameters hitboxParameters;

    public Hitbox(int x, int y, HitboxParameters hitboxParameters) {
        this.x = x;
        this.y = y;
        this.hitboxParameters = hitboxParameters;
    }

    public Hitbox(Position center, HitboxParameters hitboxParameters) {
        this.x = center.x();
        this.y = center.y();
        this.hitboxParameters = hitboxParameters;
    }

    public Position getTopLeft() {
        return new Position(
                x - hitboxParameters.width() / 2,
                y - hitboxParameters.height() / 2
        );
    }

    public Position getTopRight() {
        return new Position(
                x + hitboxParameters.width() / 2,
                y - hitboxParameters.height() / 2
        );
    }

    public Position getBottomRight() {
        return new Position(
                x + hitboxParameters.width() / 2,
                y + hitboxParameters.height() / 2
        );
    }

    public Position getBottomLeft() {
        return new Position(
                x - hitboxParameters.width() / 2,
                y + hitboxParameters.height() / 2
        );
    }

    public Point2D.Double[] getVertices() {
        Point2D.Double[] vertices = new Point2D.Double[4];

        double halfWidth = hitboxParameters.width() / 2.0;
        double halfHeight = hitboxParameters.height() / 2.0;

        double[] xOffsets = {-halfWidth, halfWidth, halfWidth, -halfWidth};
        double[] yOffsets = {-halfHeight, -halfHeight, halfHeight, halfHeight};

        double angle = hitboxParameters.angle();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        for (int i = 0; i < 4; i++) {
            double rotatedX = xOffsets[i] * cos - yOffsets[i] * sin;
            double rotatedY = xOffsets[i] * sin + yOffsets[i] * cos;
            vertices[i] = new Point2D.Double(x + rotatedX, y + rotatedY);
        }

        return vertices;
    }

    public void moveCenterTo(Position position) {
        this.x = position.x();
        this.y = position.y();
    }

    public boolean intersects(Hitbox other) {
        Area thisArea = createArea(this);
        Area otherArea = createArea(other);

        thisArea.intersect(otherArea);
        return !thisArea.isEmpty();
    }

    private Area createArea(Hitbox hitbox) {
        Path2D path = new Path2D.Double();
        Point2D.Double[] vertices = hitbox.getVertices();

        path.moveTo(vertices[0].x, vertices[0].y);
        for (int i = 1; i < vertices.length; i++) {
            path.lineTo(vertices[i].x, vertices[i].y);
        }
        path.closePath();
        return new Area(path);
    }

    @Override
    public Hitbox clone() {
        try {
            Hitbox cloned = (Hitbox) super.clone();
            cloned.x = this.x;
            cloned.y = this.y;
            cloned.hitboxParameters = this.hitboxParameters.clone();
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public int getCentreX() {
        return x;
    }

    public int getCentreY() {
        return y;
    }

    public HitboxParameters getHitboxParameters() {
        return hitboxParameters.clone();
    }

    public Position getCenter() {
        return new Position(x, y);
    }
}
