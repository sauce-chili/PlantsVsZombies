package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.damage.MeleeDamage;

import java.util.*;
import java.util.stream.Collectors;

public class MeleeAttack<E extends Mob> extends AttackStrategy {

    private final Field field;
    private Class<E> enemyType;
    private long damage;

    public MeleeAttack(
            Field field,
            long attackTimeout,
            Class<E> enemyType,
            long damage
    ) {
        super(attackTimeout);
        this.field = field;
        this.enemyType = enemyType;
        this.damage = damage;
    }

    @Override
    public boolean canBeAttack() {
        return !getEnemiesInAttackerCell().isEmpty();
    }

    private Set<E> getEnemiesInAttackerCell() {
        return getField().getCell(getAttacker().getPosition())
                .map(Set::of)
                .map(cells -> getField().findMobsInCells(cells, getEnemyType()))
                .map(s -> s.collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    @Override
    protected Set<E> getAttackedEnemies() {
        return getNearestEnemy(getEnemiesInAttackerCell())
                .map(Set::of)
                .orElse(Collections.emptySet());
    }

    private Optional<E> getNearestEnemy(Set<E> enemies) {
        return enemies.stream()
                .min(Comparator.comparingDouble(m ->
                        m.getPosition().distance(getAttacker().getPosition())
                ));
    }

    @Override
    protected MeleeDamage buildDamage(Mob attackedMob) {
        return new MeleeDamage(getAttacker(), damage);
    }

    protected Field getField() {
        return field;
    }

    protected Class<E> getEnemyType() {
        return enemyType;
    }
}
