package model.core.world;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.plant.ProducingPlantBehavior;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CellTest {

    private final Position pos = new Position(0, 0);
    private final int width = 80;
    private final int height = 100;

    @Tag("construction")
    @Test
    void shouldThrowWhenWidthOrHeightNotPositive() {
        assertThrows(IllegalArgumentException.class, () -> new Cell(pos, -1, height));
        assertThrows(IllegalArgumentException.class, () -> new Cell(pos, width, 0));
    }

    @Tag("construction")
    @Tag("geometry")
    @Test
    void shouldCreateCellCorrectly() {
        Cell cell = new Cell(pos, width, height);

        // размеры
        assertEquals(width, cell.width());
        assertEquals(height, cell.height());

        // углы
        assertEquals(pos, cell.getLeftTop());
        assertEquals(new Position(pos.x() + width, pos.y()), cell.getRightTop());
        assertEquals(new Position(pos.x(), pos.y() + height), cell.getLeftBottom());
        assertEquals(new Position(pos.x() + width, pos.y() + height), cell.getRightBottom());
    }

    @Tag("planting")
    @Test
    void shouldPlantSunflowerWhenCellIsEmpty() {
        Cell cell = new Cell(pos, width, height);
        ProducingPlantBehavior.TokenSunConsumer consumer = mock(ProducingPlantBehavior.TokenSunConsumer.class);
        SunFlower plant = new SunFlower(consumer);

        Optional<Throwable> error = cell.setPlant(plant);

        assertTrue(error.isEmpty());
        assertTrue(cell.planted());
        assertEquals(cell, plant.getCell()); // проверка связи
    }

    @Tag("planting")
    @Test
    void shouldNotPlantSunflowerWhenCellAlreadyOccupied() {
        Cell cell = new Cell(pos, width, height);
        ProducingPlantBehavior.TokenSunConsumer consumer = mock(ProducingPlantBehavior.TokenSunConsumer.class);
        SunFlower first = new SunFlower(consumer);
        SunFlower second = new SunFlower(consumer);

        cell.setPlant(first);
        Optional<Throwable> error = cell.setPlant(second);

        assertTrue(error.isPresent());
        assertEquals(cell, first.getCell());
        assertNull(second.getCell()); // второе так и не связано
    }

    @Tag("planting")
    @Test
    void shouldNotPlantSamePlantTwice() {
        Cell cell1 = new Cell(pos, width, height);
        Cell cell2 = new Cell(new Position(100, 100), width, height);

        ProducingPlantBehavior.TokenSunConsumer consumer = mock(ProducingPlantBehavior.TokenSunConsumer.class);
        SunFlower plant = new SunFlower(consumer);

        // первый раз успешно
        assertTrue(cell1.setPlant(plant).isEmpty());

        // второй раз — IllegalArgumentException (см. Plant.setCell)
        assertEquals(IllegalArgumentException.class, cell2.setPlant(plant).get().getClass());
    }

    @Tag("planting")
    @Test
    void shouldUnsetPlantAndClearCellReference() {
        Cell cell = new Cell(pos, width, height);
        ProducingPlantBehavior.TokenSunConsumer consumer = mock(ProducingPlantBehavior.TokenSunConsumer.class);
        SunFlower plant = new SunFlower(consumer);

        cell.setPlant(plant);
        boolean result = cell.unsetPlant();

        assertTrue(result);
        assertFalse(cell.planted());
        assertNull(plant.getCell()); // ссылка у растения очищена
    }

    @Tag("planting")
    @Test
    void shouldReturnFalseWhenUnsetEmptyCell() {
        Cell cell = new Cell(pos, width, height);

        boolean result = cell.unsetPlant();

        assertFalse(result);
    }

    @Tag("geometry")
    @Test
    void shouldReturnCenterPosition() {
        Cell cell = new Cell(pos, width, height);
        Position center = cell.getCenter();
        assertEquals(pos.x() + width / 2, center.x());
        assertEquals(pos.y() + height / 2, center.y());
    }

    @Tag("geometry")
    @Test
    void shouldDetectPositionInside() {
        Cell cell = new Cell(pos, width, height);
        Position inside = new Position(10, 20);
        Position outside = new Position(1000, 1000);

        assertTrue(cell.positionInCell(inside));
        assertFalse(cell.positionInCell(outside));
    }
}
