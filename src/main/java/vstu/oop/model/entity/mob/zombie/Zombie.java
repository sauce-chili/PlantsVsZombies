package vstu.oop.model.entity.mob.zombie;

import vstu.oop.model.core.world.Direction;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.movement.Movable;
import vstu.oop.model.entity.collision.Hitbox;
import vstu.oop.model.entity.mob.attacking_strategy.AttackStrategy;
import vstu.oop.model.entity.mob.damage.Damage;
import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.movement.MovementEngine;

public abstract class Zombie extends Mob {

    private final AttackStrategy attackStrategy;
    private Field field;
    private long speed;
    private final MovementEngine movementEngine;

    public Zombie(
            Hitbox hitbox,
            AttackStrategy attackStrategy,
            Field field,
            long health,
            long speed
    ) {
        super(hitbox, health);
        this.attackStrategy = attackStrategy;
        this.attackStrategy.setAttacker(this);
        this.speed = speed;
        this.field = field;
        this.movementEngine = new MovementEngine(ZOMBIE_MOVABLE);
    }

    @Override
    public void act(long currentTick) {

        if (getAttackStrategy().canBeAttack()) {
            getAttackStrategy().attack(currentTick);
            return;
        }

        movementEngine.move(currentTick);
    }

    @Override
    public void applyDamage(Damage damage) {
        // Zombie do not take damage from other zombies
        if (damage.emitter() instanceof Zombie) {
            return;
        }
        super.applyDamage(damage);
    }

    protected AttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    protected Field getField() {
        return field;
    }

    private final Movable ZOMBIE_MOVABLE = new Movable() {
        @Override
        public long getSpeed() {
            return speed;
        }

        @Override
        public Position getPosition() {
            return Zombie.this.getPosition();
        }

        @Override
        public void moveTo(Position pos) {
            setPosition(pos);
        }

        @Override
        public boolean isReachedTargetPosition() {
            return Zombie.this.getField().houseIsReached(getPosition());
        }

        @Override
        public Direction getMoveDirection() {
            return Direction.WEST;
        }
    };
}
