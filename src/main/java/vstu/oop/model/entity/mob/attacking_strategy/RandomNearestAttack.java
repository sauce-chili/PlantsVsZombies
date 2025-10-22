package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.damage.Damage;

import java.util.*;

public class RandomNearestAttack extends AttackStrategy {
    private final AttackStrategy delegate;
    private double attackProbability;

    private final static Random random = new Random();

    public RandomNearestAttack(AttackStrategy delegate, double attackProbability) {
        super(delegate.getAttackTimeout());
        this.delegate = delegate;
        if (attackProbability < 0 || attackProbability > 1) {
            throw new IllegalArgumentException("Вероятность должна быть между 0 и 1");
        }
        this.attackProbability = attackProbability;
    }

    @Override
    public void setAttacker(Mob attacker) {
        super.setAttacker(attacker);
        delegate.setAttacker(attacker);
    }

    @Override
    protected void attackEnemies(Collection<? extends Mob> enemies) {
        if (willAttack()) {
            delegate.attackEnemies(enemies);
        } else {
            System.out.println("Вероятность атаки не сработала :(");
        }
    }

    private boolean willAttack() {
        return random.nextDouble() < attackProbability;
    }

    @Override
    public boolean canBeAttack() {
        return delegate.canBeAttack();
    }

    @Override
    protected Set<? extends Mob> getAttackedEnemies() {
        return getNearestEnemy(delegate.getAttackedEnemies())
                .map(Set::of)
                .orElseGet(Collections::emptySet);
    }

    private Optional<? extends Mob> getNearestEnemy(Set<? extends Mob> enemies) {
        return enemies.stream()
                .min(Comparator.comparingDouble(m ->
                        m.getPosition().distance(delegate.getAttacker().getPosition())
                ));
    }

    @Override
    protected Damage buildDamage(Mob attackedMob) {
        return delegate.buildDamage(attackedMob);
    }
}
