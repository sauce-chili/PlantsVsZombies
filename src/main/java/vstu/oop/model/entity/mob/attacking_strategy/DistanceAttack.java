package vstu.oop.model.entity.mob.attacking_strategy;

import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.Direction;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.Mob;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DistanceAttack<E extends Mob> extends AttackStrategy{

    private final Field field;
    private Class<E> enemyType;
    private Direction attackingDirection;
    private int countAttackingCells;

    public DistanceAttack(
            Field field,
            long attackTimeout,
            Class<E> enemyType,
            Direction attackingDirection,
            int countAttackingCells
    ) {
        super(attackTimeout);
        this.field = field;
        this.enemyType = enemyType;
        this.attackingDirection = attackingDirection;
        this.countAttackingCells = countAttackingCells;
    }

    @Override
    public boolean canBeAttack() {
        return !getEnemiesInAttackedZone().isEmpty();
    }

    @Override
    public Set<E> getAttackedEnemies() {
        return getEnemiesInAttackedZone();
    }

    private Set<E> getEnemiesInAttackedZone() {
        Set<Cell> attackingZone = getField().getCellPosition(
                        getAttacker().getPosition()
                )
                .map(cp -> getField().getCells(
                        cp,
                        getAttackingDirection(),
                        getCountAttackingCells()
                ))
                .orElse(Collections.emptySet());

        return getField().findMobsInCells(attackingZone, getEnemyType())
                .collect(Collectors.toSet());
    }

    protected Direction getAttackingDirection() {
        return attackingDirection;
    }

    protected Class<E> getEnemyType() {
        return enemyType;
    }

    protected int getCountAttackingCells() {
        return countAttackingCells;
    }

    protected Field getField() {
        return field;
    }
}
