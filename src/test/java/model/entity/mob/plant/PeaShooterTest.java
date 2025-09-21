package model.entity.mob.plant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestFieldUtil.spawnZombiesInField;

class PeaShooterTest extends AbstractPlantTest<PeaShooter> {
    @Override
    protected PeaShooter createPlant() {
        return new PeaShooter(field);
    }

    @Test
    @DisplayName("PeaShooter не атакует, когда противников нет на поле")
    void doesNotShootWithoutZombies() {
        PeaShooter shooter = getPlacedPlant();

        int before = field.getProjectilesContainer().size();
        shooter.act(1000);
        int after = field.getProjectilesContainer().size();

        assertEquals(before, after, "Без зомби не должно появляться снарядов");
    }

    @Test
    @DisplayName("PeaShooter атакует если зомби в радиусе атаки на той же линии")
    void shootsIfZombieInAttackRangeSameLine() {
        Cell shooterCell = anyPlantableCell();
        shooterCell.setPlant(plant);

        int shooterCol = field.getCellPosition(shooterCell).col();
        Cell zombieCell = field.getLine(field.getCellPosition(shooterCell).row())[shooterCol + 2];
        SimpleZombie zombie = new SimpleZombie(zombieCell.getCenter(), field);
        spawnZombiesInField(field, List.of(zombie));

        int before = field.getProjectilesContainer().size();
        plant.act(2000);
        assertTrue(field.getProjectilesContainer().size() > before,
                "Должен появиться хотя бы один снаряд");
    }

    @Test
    @DisplayName("PeaShooter не атакует если зомби на той же линии, но дальше радиуса атаки (6 клеток)")
    void doesNotShootIfZombieTooFarOnSameLine() {
        Cell shooterCell = anyPlantableCell();
        shooterCell.setPlant(plant);

        int shooterRow = field.getCellPosition(shooterCell).row();
        int shooterCol = field.getCellPosition(shooterCell).col();
        Cell zombieCell = field.getLine(shooterRow)[shooterCol + 7]; // за радиусом
        SimpleZombie zombie = new SimpleZombie(zombieCell.getCenter(), field);
        spawnZombiesInField(field, List.of(zombie));

        int before = field.getProjectilesContainer().size();
        plant.act(2000);
        assertEquals(before, field.getProjectilesContainer().size(),
                "Снарядов быть не должно, зомби слишком далеко");
    }

    @Test
    @DisplayName("PeaShooter не атакует если зомби в радиусе, но на другой линии")
    void doesNotShootIfZombieOnDifferentLine() {
        Cell shooterCell = anyPlantableCell();
        shooterCell.setPlant(plant);

        int shooterCol = field.getCellPosition(shooterCell).col();
        int otherRow = field.getCellPosition(shooterCell).row() + 1;
        Cell zombieCell = field.getLine(otherRow)[shooterCol + 2];
        SimpleZombie zombie = new SimpleZombie(zombieCell.getCenter(), field);
        spawnZombiesInField(field, List.of(zombie));

        int before = field.getProjectilesContainer().size();
        plant.act(2000);
        assertEquals(before, field.getProjectilesContainer().size(),
                "Снарядов быть не должно, зомби не на линии PeaShooter");
    }

    @Test
    @DisplayName("PeaShooter физически убивает зомби своими снарядами, после чего при следующем act ничего не делает")
    void killsZombieWithProjectilesThenDoesNothing() {
        Cell shooterCell = anyPlantableCell();
        shooterCell.setPlant(plant);

        int shooterRow = field.getCellPosition(shooterCell).row();
        int shooterCol = field.getCellPosition(shooterCell).col();
        Cell zombieCell = field.getLine(shooterRow)[shooterCol + 2];
        SimpleZombie zombie = new SimpleZombie(zombieCell.getCenter(), field);
        spawnZombiesInField(field, List.of(zombie));

        long time = 0;
        while (zombie.isAlive() && time < 10_000) {
            time += 500;
            plant.act(time);
            field.getProjectilesContainer().update(time); // двигаем снаряды и проверяем попадания
        }

        assertTrue(zombie.isDead(), "Зомби должен быть убит снарядами PeaShooter");

        int before = field.getProjectilesContainer().size();

        // Проверим, что после смерти зомби новые снаряды не появляются
        for (int i = 0; i < 3; i++) {
            time += 500;
            plant.act(time);
            field.getProjectilesContainer().update(time);
        }

        int after = field.getProjectilesContainer().size();
        assertTrue(before <= after,
                "После смерти зомби новые снаряды не должны появляться");
    }

    @Test
    @DisplayName("PeaShooter не атакует растения, посаженные перед ним")
    void doesNotShootFriendlyPlant() {
        Cell shooterCell = anyPlantableCell();
        shooterCell.setPlant(plant);

        int shooterRow = field.getCellPosition(shooterCell).row();
        int shooterCol = field.getCellPosition(shooterCell).col();
        Cell blockerCell = field.getLine(shooterRow)[shooterCol + 1];
        blockerCell.setPlant(new SunFlower(amount -> {}));

        int before = field.getProjectilesContainer().size();
        plant.act(2000);
        assertEquals(before, field.getProjectilesContainer().size(),
                "PeaShooter не должен атаковать другие растения");
    }
}
