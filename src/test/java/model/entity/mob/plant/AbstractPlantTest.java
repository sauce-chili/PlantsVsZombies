package model.entity.mob.plant;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.plant.Plant;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestFieldUtil.createDefaultField;

public abstract class AbstractPlantTest<P extends Plant> {

    protected Field field;
    protected P plant;

    @BeforeEach
    void setUpField() {
        field = createDefaultField();
        plant = createPlant();
    }

    @AfterEach
    void tearDown() {
        field.clear();
    }

    protected P getPlacedPlant() {
        Cell cell = anyPlantableCell();
        P plant = createPlant();
        cell.setPlant(plant);

        return plant;
    }

    protected abstract P createPlant();

    protected Cell anyPlantableCell() {
        return field.getLine(2)[2]; // нормальная ячейка, не DisabledCell
    }

    // --- act ---

    @Test
    @DisplayName("act у непосаженного растения выбрасывает IllegalStateException")
    void actThrowsIfNotPlanted() {
        assertThrows(IllegalStateException.class, () -> plant.act(0));
    }

    @Test
    @DisplayName("act у посаженного растения не выбрасывает исключений")
    void actDoesNotThrowIfPlanted() {
        Cell cell = anyPlantableCell();
        assertTrue(cell.setPlant(plant).isEmpty());
        assertDoesNotThrow(() -> plant.act(0));
    }

    // --- hitbox ---

    @Test
    @DisplayName("hitbox до посадки находится в Integer.MIN_VALUE, после посадки в центре клетки")
    void hitboxBeforePlantingAtMinValueAfterPlantingAtCellCenter() {
        var before = plant.getHitbox().getCenter();
        assertEquals(Integer.MIN_VALUE, before.x());
        assertEquals(Integer.MIN_VALUE, before.y());

        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        var after = plant.getHitbox().getCenter();
        assertEquals(cell.getCenter(), after);
    }

    // --- planting ---

    @Test
    @DisplayName("посадка помещает растение в клетку")
    void plantingPutsPlantIntoCell() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);
        assertTrue(cell.getPlant().isPresent());
        assertSame(plant, cell.getPlant().get());
    }

    // --- digUp ---

    @Test
    @DisplayName("digUp у непосаженного растения ничего не делает и не выбрасывает исключение")
    void digUpDoesNothingIfNotPlanted() {
        assertDoesNotThrow(() -> plant.digUp());
        assertNull(plant.getCell(), "У непосаженного растения cell должен быть null");
    }

    @Test
    @DisplayName("digUp у посаженного растения выбрасывает IllegalStateException")
    void digUpThrowsIfPlanted() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        assertThrows(IllegalStateException.class, () -> plant.digUp());
        assertSame(cell, plant.getCell(), "Растение остаётся посаженным");
    }

    @Test
    @DisplayName("unsetPlant у клетки успешно выкапывает растение")
    void unsetPlantViaCellUnsetsPlant() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);
        assertTrue(cell.planted());

        cell.unsetPlant();

        assertFalse(cell.planted());
        assertNull(plant.getCell());
    }
}
