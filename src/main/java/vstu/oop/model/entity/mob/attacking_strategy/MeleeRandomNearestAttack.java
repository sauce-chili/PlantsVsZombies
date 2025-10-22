package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.Mob;

import java.util.Collection;
import java.util.Random;

public class MeleeRandomNearestAttack<E extends Mob> extends MeleeAttack<E> {

    private static final Random random = new Random();

    private double attackProbability;

    public MeleeRandomNearestAttack(
            Field field,
            long attackTimeout,
            long damage,
            Class<E> enemyType,
            double attackProbability
    ) {
        super(field, attackTimeout, enemyType, damage);
        if (attackProbability < 0 || attackProbability > 1) {
            throw new IllegalArgumentException("Вероятность должна быть между 0 и 1");
        }
        this.attackProbability = attackProbability;
    }

    @Override
    protected void attackEnemies(Collection<? extends Mob> enemies) {
        if (willAttack()) {
            super.attackEnemies(enemies);
        } else {
            System.out.println("Вероятность атаки не сработала :(");
        }
    }

    private boolean willAttack() {
        return random.nextDouble() < attackProbability;
    }
}
