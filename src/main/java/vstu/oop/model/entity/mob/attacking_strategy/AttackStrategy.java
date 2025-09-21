package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.entity.mob.damage.Damage;
import vstu.oop.model.entity.mob.Mob;

import java.util.Collection;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public abstract class AttackStrategy {

    private boolean isAttacking = false;
    private Long lastAttackTime;
    private long attackTimeout;

    private Mob attacker;

    public AttackStrategy(long attackTimeout) {
        this.attackTimeout = attackTimeout;
    }

    /**
     * Производит атаку противников, если позволяет timeout и есть доступные для атаки противники, определяется через {@link AttackStrategy#canBeAttack()}
     * */
    public final boolean attack(long tick) {
        requireNonNull(getAttacker(), "Attacker is not set");
        if (attackTimeoutNotExceeded(tick)) {
            return false;
        }

        if (canNotBeAttack()) {
            setAttackingState(false);
            return false;
        }

        Set<? extends Mob> attackedEnemy = getAttackedEnemies();

        if (attackedEnemy.isEmpty()) {
            return false;
        }

        setAttackingState(true);
        attackEnemies(attackedEnemy);

        setLastAttackTime(tick);

        return true;
    }

    protected void attackEnemies(Collection<? extends Mob> enemies) {
        enemies.forEach(e -> {
            Damage damage = buildDamage(e);
            e.applyDamage(damage);
        });
    }

    public abstract boolean canBeAttack();

    public final boolean canNotBeAttack() {
        return !canBeAttack();
    }

    protected abstract Set<? extends Mob> getAttackedEnemies();

    protected abstract Damage buildDamage(Mob attackedMob);

    public boolean attackTimeoutExceeded(long currentTick) {
        if (getLastAttackTime() == null) {
            return true;
        }

        return currentTick - getLastAttackTime() >= getAttackTimeout();
    }

    public boolean attackTimeoutNotExceeded(long currentTick) {
        return !attackTimeoutExceeded(currentTick);
    }

    public boolean isAttackingState() {
        return isAttacking;
    }

    protected Mob getAttacker() {
        return attacker;
    }

    public void setAttacker(Mob attacker) {
        requireNonNull(attacker, "Attacker must be present");
        if (getAttacker() != null) {
            throw new IllegalArgumentException("Attacker can be set only once");
        }
        this.attacker = attacker;
    }

    public long getAttackTimeout() {
        return attackTimeout;
    }

    protected void setAttackTimeout(long attackTimeout) {
        this.attackTimeout = attackTimeout;
    }

    public Long getLastAttackTime() {
        return lastAttackTime;
    }

    protected void setLastAttackTime(Long lastAttackTime) {
        this.lastAttackTime = lastAttackTime;
    }

    protected void setAttackingState(boolean isAttacking) {
        this.isAttacking = isAttacking;
    }
}
