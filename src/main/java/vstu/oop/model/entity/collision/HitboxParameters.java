package vstu.oop.model.entity.collision;

public class HitboxParameters implements Cloneable {
    private int width, height;
    private double angle;

    public HitboxParameters(int width, int height, double angle) {
        this.width = width;
        this.height = height;
        this.angle = angle;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public double angle() {
        return angle;
    }

    @Override
    public HitboxParameters clone() {
        try {
            HitboxParameters clone = (HitboxParameters) super.clone();
            clone.width = this.width;
            clone.height = this.height;
            clone.angle = this.angle;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
