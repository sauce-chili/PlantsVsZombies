package vstu.oop.model.entity.mob;

import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.entity.GameObject;
import vstu.oop.model.entity.mob.damage.Damage;

import java.util.function.Consumer;

import static java.util.Objects.isNull;

public abstract class Mob extends GameObject {

    private long health;
    private final long initialHealth;

    public Mob(Hitbox hitbox, long health) {
        super(hitbox);
        this.health = health;
        this.initialHealth = health;
    }

    public void applyDamage(Damage damage) {
        if (isNull(damage.emitter()) || damage.emitter() == this) {
            return;
        }
        if ((health -= damage.amountHealthDamage()) < 0) {
            health = 0L;
        }
    }

    public boolean kill() {
        health = 0L;
        return true;
    }

    public boolean isAlive() {
        return getHealth() > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    public long getHealth() {
        return health;
    }

    public long getInitialHealth() {
        return initialHealth;
    }
}
