package vstu.oop.model.entity.mob.damage;

import vstu.oop.model.core.world.Direction;
import vstu.oop.model.entity.mob.movement.Movable;
import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.entity.collision.HitboxParameters;
import vstu.oop.model.entity.GameObject;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.movement.MovementEngine;

public abstract class Projectile extends GameObject implements Damage {

    private long damage;
    private boolean isActive = true;
    private Mob emitter;
    private long speed;

    protected MovementEngine movementEngine;
    protected Field field;
    protected Direction direction;

    public Projectile(
            Position pos,
            Field field,
            Direction movementDirection,
            long speed,
            long amountDamage
    ) {
        super(new Hitbox(pos, new HitboxParameters(25, 25, 0)));
        this.direction = movementDirection;
        this.speed = speed;
        this.field = field;
        this.damage = amountDamage;
        this.movementEngine = new MovementEngine(PROJECTILE_MOVABLE);
    }

    @Override
    public void act(long currentTick) {
        boolean wasHit = dealDamageOnHit();
        if (wasHit) {
            isActive = false;
            return;
        }

        boolean targetPositionIsReached = movementEngine.move(currentTick);
        if (targetPositionIsReached) {
            isActive = false;
        }
    }

    protected abstract boolean dealDamageOnHit();

    @Override
    public Mob emitter() {
        return emitter;
    }

    @Override
    public Long amountHealthDamage() {
        return damage;
    }

    public void setEmitter(Mob emitter) {
        this.emitter = emitter;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isInactive() {
        return !isActive();
    }

    protected Direction getMovementDirection() {
        return direction;
    }

    protected long getSpeed() {
        return speed;
    }

    protected Field getField() {
        return field;
    }

    private final Movable PROJECTILE_MOVABLE = new Movable() {
        @Override
        public long getSpeed() {
            return Projectile.this.getSpeed();
        }

        @Override
        public Position getPosition() {
            return Projectile.this.getPosition();
        }

        @Override
        public void moveTo(Position pos) {
            setPosition(pos);
        }

        @Override
        public boolean isReachedTargetPosition() {
            return Projectile.this.getField().outsideOfField(getPosition());
        }

        @Override
        public Direction getMoveDirection() {
            return Projectile.this.getMovementDirection();
        }
    };
}
