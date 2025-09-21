package model.entity.mob.plant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;
import vstu.oop.model.entity.mob.zombie.Zombie;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestFieldUtil.spawnZombiesInField;
import static test_util.TestFieldUtil.getFieldCharacteristics;

public class PotatoMineTest extends AbstractPlantTest<PotatoMine>{
    @Override
    protected PotatoMine createPlant() {
        return new PotatoMine(field);
    }

    /**
     * Устанавливает plantedTick так, будто мина была посажена millisAgo миллисекунд назад.
     */
    private void setPlantedTickInPast(PotatoMine mine, long millisAgo) {
        try {
            Object behavior = mine.behavior();
            Field f = behavior.getClass().getDeclaredField("plantedTick");
            f.setAccessible(true);
            long now = System.currentTimeMillis();
            f.set(behavior, now - millisAgo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("При посадке у поведения сохраняется тик посадки, isGrowth=false, через 11 секунд isGrowth=true")
    void plantingStoresTickAndGrowthChangesAfterTimeout() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        assertFalse(plant.behavior().isGrowth(), "Сразу после посадки мина не активна");

        setPlantedTickInPast(plant, 11_000);
        plant.act(System.currentTimeMillis());

        assertTrue(plant.behavior().isGrowth(), "Через 10 секунд мина становится активной");
    }

    @Test
    @DisplayName("Нет зомби на поле — мина ничего не делает (до и после роста)")
    void doesNothingWithoutZombies() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        // до роста
        plant.act(System.currentTimeMillis());
        assertFalse(plant.behavior().wasExplosion());
        assertFalse(plant.isDead());

        // после роста
        setPlantedTickInPast(plant, 11_000);
        plant.act(System.currentTimeMillis());
        assertTrue(plant.behavior().isGrowth());
        assertFalse(plant.behavior().wasExplosion());
        assertFalse(plant.isDead());
    }

    @Test
    @DisplayName("Мина не выросла, зомби в той же ячейке — ничего не происходит")
    void notGrownZombieSameCellDoesNothing() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        SimpleZombie zombie = new SimpleZombie(cell.getCenter(), field);
        spawnZombiesInField(field, List.of(zombie));

        plant.act(System.currentTimeMillis());

        assertFalse(plant.behavior().wasExplosion());
        assertTrue(zombie.isAlive());
    }

    @Test
    @DisplayName("Мина выросла, зомби в другой линии — ничего не происходит")
    void grownZombieOtherLineDoesNothing() {
        Cell mineCell = anyPlantableCell();
        mineCell.setPlant(plant);

        setPlantedTickInPast(plant, 11_000);

        int row = field.getCellPosition(mineCell).row();
        int col = field.getCellPosition(mineCell).col();
        Cell otherLineCell = field.getLine(row + 1)[col];
        SimpleZombie zombie = new SimpleZombie(otherLineCell.getCenter(), field);
        spawnZombiesInField(field, List.of(zombie));

        plant.act(System.currentTimeMillis());

        assertFalse(plant.behavior().wasExplosion());
        assertTrue(zombie.isAlive());
    }

    @Test
    @DisplayName("Мина выросла, рядом 4 зомби — убивает всех в радиусе, сама умирает через 2 секунды")
    void grownExplodesKillsNearbyZombiesAndDies() throws InterruptedException {
        Cell mineCell = anyPlantableCell();
        mineCell.setPlant(plant);

        setPlantedTickInPast(plant, 11_000);

        // 1) Зомби на клетке с миной
        SimpleZombie z1 = new SimpleZombie(mineCell.getCenter(), field);

        // 2) Зомби позади на CELL_WIDTH / 2
        Position posBehind = new Position(
                mineCell.getCenter().x() - getFieldCharacteristics(field).cellWidth() / 2,
                mineCell.getCenter().y()
        );
        SimpleZombie z2 = new SimpleZombie(posBehind, field);

        spawnZombiesInField(field, List.of(z1, z2));

        // act -> взрыв
        plant.act(System.currentTimeMillis());

        assertTrue(plant.behavior().wasExplosion(), "Мина должна взорваться");
        assertTrue(z1.isDead(), "Зомби на клетке с миной должен умереть");
        assertTrue(z2.isDead(), "Зомби позади мины должен умереть");

        // через 2 секунды мина умирает
        Thread.sleep(2000L);
        plant.act(System.currentTimeMillis() + 3500);
        assertTrue(plant.isDead(), "Мина должна умереть после взрыва");

        // поле обновляется — ни мины, ни живых зомби
        field.update(System.currentTimeMillis() + 3000);
        assertTrue(field.getZombies().allMatch(Zombie::isDead));
        assertFalse(mineCell.planted(), "Клетка должна освободиться");
    }
}
