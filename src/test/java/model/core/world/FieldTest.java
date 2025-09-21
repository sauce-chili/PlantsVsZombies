package model.core.world;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.EnumSource;
import vstu.oop.model.core.world.*;
import vstu.oop.model.entity.mob.damage.Projectile;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;
import vstu.oop.model.entity.mob.zombie.Zombie;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static test_util.TestFieldUtil.spawnZombiesInField;

public class FieldTest {
    private Field field;

    @BeforeEach
    void setUp() {
        FieldCharacteristics fc = vstu.oop.utils.Constance.defaultFieldCharacteristics;
        Cell[][] cells = new Cell[fc.rowsOfCell()][fc.columnsOfCell()];
        for (int row = 0; row < fc.rowsOfCell(); row++) {
            for (int col = 0; col < fc.columnsOfCell(); col++) {
                Position pos = new Position(fc.plantableCellsStartingWithX() + col * fc.cellWidth(),
                        fc.plantableCellsStartingWithY() + row * fc.cellHeight());
                cells[row][col] = (col == 0 || col == fc.columnsOfCell() - 1)
                        ? new DisabledCell(pos, fc.cellWidth(), fc.cellHeight())
                        : new Cell(pos, fc.cellWidth(), fc.cellHeight());
            }
        }
        field = new Field(fc, cells);
    }

    // -------- clear --------

    @Test
    @DisplayName("clear очищает зомби и растения с поля")
    void clearRemovesZombiesAndPlants() {
        Zombie z = new SimpleZombie(new Position(2000, 200), field);
        spawnZombiesInField(field, Set.of(z));

        Cell c = field.getLine(0)[1];
        Plant sunflower = new SunFlower(amount -> {});
        c.setPlant(sunflower);

        assertTrue(field.getZombies().findAny().isPresent());
        assertTrue(field.getPlants().findAny().isPresent());

        field.clear();

        assertFalse(field.getZombies().findAny().isPresent(), "Все зомби должны быть удалены");
        assertFalse(c.planted(), "Клетка должна стать пустой");
        assertFalse(field.getPlants().findAny().isPresent());
    }

    // -------- getCellPosition(Position) --------

    @Test
    @DisplayName("getCellPosition возвращает позицию для координат внутри поля")
    void getCellPositionFromValidPosition() {
        Cell c = field.getLine(0)[1];
        Position pos = c.getCenter();

        Optional<Field.CellPosition> cp = field.getCellPosition(pos);

        assertTrue(cp.isPresent());
        assertEquals(c, field.getCell(cp.get()));
    }

    @Test
    @DisplayName("getCellPosition возвращает empty для координат вне поля")
    void getCellPositionFromInvalidPosition() {
        Position outside = new Position(-100, -100);

        Optional<Field.CellPosition> cp = field.getCellPosition(outside);

        assertTrue(cp.isEmpty(), "Координаты вне поля должны возвращать empty");
    }

    // -------- getCellPosition(Cell) --------

    @Test
    @DisplayName("getCellPosition(Cell) возвращает позицию клетки")
    void getCellPositionFromCell() {
        Cell c = field.getLine(2)[2];
        Field.CellPosition cp = field.getCellPosition(c);

        assertEquals(c, field.getCell(cp));
    }

    // -------- getCell(CellPosition) --------

    @Test
    @DisplayName("getCell(CellPosition) возвращает правильную клетку по row/col")
    void getCellFromCellPosition() {
        Cell expected = field.getLine(1)[3];
        Field.CellPosition cp = field.new CellPosition(1, 3);

        Cell actual = field.getCell(cp);

        assertEquals(expected, actual);
    }

    // -------- getCells --------

    @ParameterizedTest(name = "getCells корректно возвращает ячейки в направлении {0}")
    @EnumSource(Direction.class)
    void getCellsReturnsCellsInAllDirections(Direction dir) {
        var startPos = field.getCellPosition(field.getLine(2)[2]);
        Set<Cell> cells = field.getCells(startPos, dir, 3);

        assertFalse(cells.isEmpty(), "Направление " + dir + " должно возвращать хотя бы стартовую клетку");
        assertTrue(cells.size() <= 3, "В направлении " + dir + " не должно быть больше чем запрошено");
    }

    @ParameterizedTest(name = "getCells ограничивается границей в направлении {0}")
    @EnumSource(Direction.class)
    void getCellsStopsAtBorder(Direction dir) {
        // берём угловую ячейку (0,0) => гарантированно будет упираться в границу хотя бы по одному направлению
        var corner = field.getCellPosition(field.getLine(0)[0]);
        Set<Cell> cells = field.getCells(corner, dir, 5);

        assertTrue(cells.size() >= 1, "Должна быть хотя бы стартовая клетка");
        assertTrue(cells.size() <= 5, "Не должно выходить за лимит");
    }

    // -------- Поисковые запросы --------

    @Test
    @DisplayName("findMobsInCells возвращает только запрошенный тип (растения или зомби)")
    void findMobsInCellsReturnsRequestedTypeOnly() {
        Cell c1 = field.getLine(0)[1];
        Cell c2 = field.getLine(1)[1];

        Plant sunflower = new SunFlower(amount -> {});
        Plant peaShooter = new PeaShooter(field);
        Zombie zombie = new SimpleZombie(c2.getCenter(), field);

        c1.setPlant(sunflower);
        c2.setPlant(peaShooter);
        spawnZombiesInField(field, Set.of(zombie));

        // ищем только SunFlower
        var sunflowers = field.findMobsInCells(Set.of(c1, c2), SunFlower.class).collect(Collectors.toSet());
        assertTrue(sunflowers.contains(sunflower));
        assertFalse(sunflowers.contains(peaShooter));
        assertFalse(sunflowers.contains(zombie));

        // ищем только растения вообще
        var plants = field.findMobsInCells(Set.of(c1, c2), Plant.class).collect(Collectors.toSet());
        assertTrue(plants.contains(sunflower));
        assertTrue(plants.contains(peaShooter));
        assertFalse(plants.contains(zombie));

        // ищем только зомби

        var zombies = field.findMobsInCells(Set.of(c1, c2), Zombie.class).collect(Collectors.toSet());
        assertTrue(zombies.contains(zombie));
        assertFalse(zombies.contains(sunflower));
        assertFalse(zombies.contains(peaShooter));
    }

    @Test
    @DisplayName("findMobsInCellsBy фильтрует по условию")
    void findMobsInCellsByFilters() {
        Cell c = field.getLine(0)[1];
        Plant sunflower = new SunFlower(amount -> {});
        c.setPlant(sunflower);

        var result = field.findMobsInCellsBy(Set.of(c), Plant.class, p -> false).toList();
        assertTrue(result.isEmpty());
    }

    // -------- update --------

    @Test
    @DisplayName("update вызывает act у зомби и растений")
    void updateCallsActOnZombiesAndPlants() {
        Zombie zombie = mock(SimpleZombie.class);
        when(zombie.isDead()).thenReturn(false);
        when(zombie.getPosition()).thenReturn(new Position(2000, 200));

        Plant plant = mock(SunFlower.class);
        when(plant.isDead()).thenReturn(false);
        when(plant.getPosition()).thenReturn(new Position(100, 100));

        spawnZombiesInField(field, Set.of(zombie));
        Cell c = field.getLine(0)[1];
        c.setPlant(plant);

        field.update(999);

        verify(zombie, atLeastOnce()).act(anyLong());
        verify(plant, atLeastOnce()).act(anyLong());
    }

    @Test
    @DisplayName("update удаляет мертвых зомби и растения")
    void updateRemovesDeadMobs() {
        Zombie z = new SimpleZombie(new Position(1000, 200), field);
        spawnZombiesInField(field, Set.of(z));
        z.kill();

        Cell c = field.getLine(0)[1];
        Plant sunflower = new SunFlower(amount -> {});
        c.setPlant(sunflower);
        sunflower.kill();

        field.update(111);

        assertFalse(field.getZombies().anyMatch(Zombie::isDead));
        assertFalse(c.planted());
    }

    @Test
    @DisplayName("Комплексный update: act у живых, удаление мертвых, обновление снарядов")
    void complexUpdate() {
        Zombie liveZombie = mock(SimpleZombie.class);
        when(liveZombie.isDead()).thenReturn(false);
        when(liveZombie.getPosition()).thenReturn(new Position(2000, 200));
        spawnZombiesInField(field, Set.of(liveZombie));

        Zombie deadZombie = new SimpleZombie(new Position(2000, 300), field);
        deadZombie.kill();
        spawnZombiesInField(field, Set.of(deadZombie));

        Plant livePlant = mock(SunFlower.class);
        when(livePlant.isDead()).thenReturn(false);
        when(livePlant.getPosition()).thenReturn(new Position(100, 100));
        field.getLine(0)[1].setPlant(livePlant);

        Plant deadPlant = new SunFlower(amount -> {});
        field.getLine(1)[1].setPlant(deadPlant);
        deadPlant.kill();

        Projectile activeProjectile = mock(Projectile.class);
        when(activeProjectile.isActive()).thenReturn(true);
        Projectile inactiveProjectile = mock(Projectile.class);
        when(inactiveProjectile.isInactive()).thenReturn(true);

        ProjectileContainer container = field.getProjectilesContainer();
        container.add(activeProjectile);
        container.add(inactiveProjectile);

        field.update(555);

        verify(liveZombie, atLeastOnce()).act(anyLong());
        verify(livePlant, atLeastOnce()).act(anyLong());

        assertFalse(field.getZombies().anyMatch(Zombie::isDead));
        assertFalse(field.getLine(1)[1].planted());

        assertTrue(container.getProjectilesSnapshot().contains(activeProjectile));
        assertFalse(container.getProjectilesSnapshot().contains(inactiveProjectile));
    }
}
