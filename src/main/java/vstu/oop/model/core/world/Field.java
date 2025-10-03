package vstu.oop.model.core.world;

import vstu.oop.model.entity.mob.Mob;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.zombie.Zombie;
import vstu.oop.utils.Pair;
import vstu.oop.utils.Util;

import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class Field {

    private final FieldCharacteristics params;
    private final Cell[][] cellGrid;

    private final List<Zombie> zombies;
    private final ProjectileContainer projectilesContainer;

    public Field(FieldCharacteristics characteristics, Cell[][] cells) {
        requireNonNull(characteristics);
        requireNonNull(cells);

        if (cells.length != characteristics.rowsOfCell()) {
            throw new IllegalArgumentException("cells grid must have exactly " + characteristics.rowsOfCell() + " rowsOfCell");
        }

        // Проверяем, что переданный ячейки соответствуют характеристикам поля
        for (int row = 0; row < characteristics.rowsOfCell(); row++) {
            Cell[] rowCells = cells[row];
            if (rowCells == null || rowCells.length != characteristics.columnsOfCell()) {
                throw new IllegalArgumentException("Each row in cellsGrid must have exactly " + characteristics.columnsOfCell() + " columnsOfCell");
            }
            for (int col = 0; col < characteristics.columnsOfCell(); col++) {
                Cell cell = rowCells[col];
                if (cell == null) {
                    throw new IllegalArgumentException("Cell at position (" + row + ", " + col + ") is null");
                }
                if (cell.width() != characteristics.cellWidth() || cell.height() != characteristics.cellHeight()) {
                    throw new IllegalArgumentException("Cell at position (" + row + ", " + col + ") has incorrect dimensions");
                }
            }
        }

        cellGrid = cells;
        params = characteristics;

        projectilesContainer = new ProjectileContainer();

        zombies = new ArrayList<>();
    }

    public void update(long currentTick) {
        projectilesContainer.update(currentTick); // обновляем снаряды
        getMobs().forEach(mob -> mob.act(currentTick)); // запрашиваем активность у мобов
        removeDiadMobs(); // удаляем мертвых мобов
    }

    private Stream<Mob> getMobs() {
        return Stream.concat(getPlants(), getZombies());
    }

    private void removeDiadMobs() {

        getPlants().filter(Plant::isDead)
                .forEach(p -> p.getCell().unsetPlant());

        Set<Zombie> diadZombie = getZombies()
                .filter(Zombie::isDead)
                .collect(Collectors.toSet());

        zombies.removeAll(diadZombie);
    }

    public <M extends Mob> Stream<M> findMobsInCells(Set<Cell> cells, Class<M> mobType) {
        return findMobsInCellsBy(cells, mobType, m -> true);
    }

    public <M extends Mob> Stream<M> findMobsInCellsBy(
            Set<Cell> cells,
            Class<M> mobType,
            Predicate<M> filter
    ) {
        return getMobs()
                .filter(mobType::isInstance)
                .map(mobType::cast)
                .filter(m -> getCell(m.getPosition())
                        .map(cells::contains)
                        .orElse(false)
                )
                .filter(filter);
    }

    public Optional<Cell> getCell(Position pos) {
        return getCellPosition(pos)
                .map(cp -> cellGrid[cp.row()][cp.col()]);
    }

    public CellPosition getCellPosition(Cell cell) {
        return getCellPosition(cell.getCenter()).orElseThrow();
    }

    public Optional<CellPosition> getCellPosition(Position pos) {
        if (pos == null) return Optional.empty();

        int column = (pos.x() - params.plantableCellsStartingWithX()) / params.cellWidth();
        int row = (pos.y() - params.plantableCellsStartingWithY()) / params.cellHeight();

        if (row < 0 || row >= params.rowsOfCell() || column < 0 || column >= params.columnsOfCell()) {
            return Optional.empty();
        }

        return Optional.of(new CellPosition(row, column));
    }

    public Cell getCell(CellPosition pos) {
        return cellGrid[pos.row()][pos.col()];
    }

    /**
     * Предоставляет указанное кол-во ячеек от {@code pos} в направлении {@code dir}, если это возможно.</br>
     * Ячейка с позицией {@code pos} включается в выборку.
     */
    public Set<Cell> getCells(CellPosition pos, Direction dir, int countCells) {
        return IntStream.rangeClosed(0, countCells - 1)
                .mapToObj(shift -> pos.move(dir, shift))
                .takeWhile(Optional::isPresent)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(cp -> cellGrid[cp.row()][cp.col()])
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Cell[] getLine(int line) {
        return cellGrid[line];
    }

    public Stream<Cell> getCells() {
        return Arrays.stream(cellGrid).flatMap(Stream::of);
    }

    public void addZombies(Collection<Pair<Integer, Function<Position, Zombie>>> zombieSpawnersOnLine) {
        requireNonNull(zombieSpawnersOnLine);

        Set<Zombie> zombies = zombieSpawnersOnLine.stream()
                .map(entry -> {
                    int line = entry.first();
                    Function<Position, Zombie> spawner = entry.second();

                    Cell c = getCellToSpawnZombie(line);
                    int y = c.getCenter().y();
                    int x = c.getRightBottom().x();
                    Position spawnPos = new Position(x, y);

                    return spawner.apply(spawnPos);
                })
                .collect(Collectors.toUnmodifiableSet());

        spawnZombies(zombies);
    }

    private Cell getCellToSpawnZombie(int line) {
        int idxLastCell = params.columnsOfCell() - 1;
        return cellGrid[line][idxLastCell];
    }

    // существует для получения через рефлексию в тестах
    private void spawnZombies(Collection<Zombie> zombies) {
        requireNonNull(zombies);
        this.zombies.addAll(zombies);
    }

    public ProjectileContainer getProjectilesContainer() {
        return projectilesContainer;
    }

    public Stream<Zombie> getZombies() {
        return zombies.stream();
    }

    public Stream<Plant> getPlants() {
        return getCells()
                .map(Cell::getPlant)
                // на случай race condition
                .map(opt -> opt.orElse(null))
                .filter(Objects::nonNull);
    }

    // существует для получения через рефлексию в тестах
    private FieldCharacteristics getCharacteristics() {
        return params;
    }

    public List<Plant> getPlantSnapshot() {
        return getPlants().toList();
    }

    public List<Zombie> getZombieSnapshot() {
        return getZombies().toList();
    }

    public boolean houseIsReached(Position pos) {
        return pos.x() <= params.plantableCellsStartingWithX();
    }

    public boolean anyZombieReachedHouse() {
        return getZombies().anyMatch(z -> houseIsReached(z.getPosition()));
    }

    public boolean allZombieDied() {
        return getZombies().allMatch(Zombie::isDead);
    }

    public boolean outsideOfField(Position pos) {
        return (pos.x() < 0 || pos.y() < 0)
                ||
                (pos.x() >= params.width() || pos.y() >= params.height());
    }

    public void clear() {
        zombies.clear();
        projectilesContainer.stopProcessingAndClear();
        getPlants().forEach(p -> p.getCell().unsetPlant());
    }

    public final class CellPosition {
        private final int row;
        private final int col;

        public CellPosition(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int row() {
            return row;
        }

        public int col() {
            return col;
        }

        public Optional<CellPosition> move(
                Direction dir,
                int shift
        ) {

            int newRow = switch (dir) {
                case NORTH, NORTH_EAST, NORTH_WEST -> row - shift;
                case SOUTH, SOUTH_EAST, SOUTH_WEST -> row + shift;
                default -> row; // Для EAST и WEST оставляем без изменения
            };

            int newCol = switch (dir) {
                case EAST, NORTH_EAST, SOUTH_EAST -> col + shift;
                case WEST, NORTH_WEST, SOUTH_WEST -> col - shift;
                default -> col; // Для NORTH и SOUTH оставляем без изменения
            };

            return (newRow >= 0 && newRow < params.rowsOfCell()) && (newCol >= 0 && newCol < params.columnsOfCell()) ?
                    Optional.of(new CellPosition(newRow, newCol)) : Optional.empty();
        }

        @Override
        public String toString() {
            return String.format("CellPosition[row=%d, col=%d]", row, col);
        }
    }
}
