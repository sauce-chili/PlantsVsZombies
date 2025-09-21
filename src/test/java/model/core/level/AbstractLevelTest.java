package model.core.level;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vstu.oop.model.core.GameState;
import vstu.oop.model.core.level.Level;
import vstu.oop.model.core.level.WaveSlot;
import vstu.oop.model.core.level.ZombieSpawner;
import vstu.oop.model.core.player.PlantCatalogue;
import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.world.*;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;
import vstu.oop.model.entity.mob.zombie.Zombie;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static test_util.TestFieldUtil.*;

public abstract class AbstractLevelTest<L extends Level> {
    protected L level;
    protected Field field;
    protected Player player;
    protected ZombieSpawner spawner;

    protected abstract L newLevel();

    protected abstract Set<Class<? extends Plant>> expectedAllowedPlants();

    protected abstract int expectedInitialSunTokens();

    protected abstract List<WaveSlot> expectedWaves();

    @BeforeEach
    void setUp() throws Exception {
        level = newLevel();
        field = level.provideField();
        player = level.providePlayer();
        spawner = level.provideZombieSpawner();
    }

    @Test
    @DisplayName("Изначальное состояние уровня — INITIAL")
    void initialStateIsInitialUntilGameStart() {
        assertEquals(GameState.INITIAL, level.getGameState());
    }

    @Test
    @DisplayName("Каталог игрока и стартовый баланс корректно инициализированы")
    void catalogueAndBalanceInitialized() {
        PlantCatalogue catalogue = player.getCatalogue();
        Set<Class<? extends Plant>> allowed =
                catalogue.getPlantsInfo().stream().map(info -> info.getImplementation()).collect(Collectors.toSet());
        assertEquals(expectedAllowedPlants(), allowed);
        assertEquals(expectedInitialSunTokens(), player.getBalance());
    }

    @Test
    @DisplayName("Поле содержит отключённые крайние колонки и имеет правильные размеры")
    void fieldGridHasDisabledBorderColumnsAndCorrectDimensions() {
        FieldCharacteristics fc = getFieldCharacteristics(field);
        assertTrue(fc.columnsOfCell() >= 3);
        for (int row = 0; row < fc.rowsOfCell(); row++) {
            Cell left = field.getLine(row)[0];
            Cell right = field.getLine(row)[fc.columnsOfCell() - 1];
            assertInstanceOf(DisabledCell.class, left);
            assertInstanceOf(DisabledCell.class, right);
        }
    }

    @Test
    @DisplayName("После старта игры состояние — IN_PROGRESS")
    void afterGameStartWithoutFailOrWinStateIsInProgress() {
        level.gameStart();
        assertEquals(GameState.IN_PROGRESS, level.getGameState());
    }

    @Test
    @DisplayName("Состояние FAILED наступает, если зомби достиг дома")
    void failedWhenAnyZombieReachesHouseLine() {
        level.gameStart();
        int xHouse = getFieldCharacteristics(field).plantableCellsStartingWithX();
        int yCenter = field.getLine(0)[1].getCenter().y();
        Zombie z = new SimpleZombie(new Position(xHouse - 1, yCenter), field);
        spawnZombiesInField(field, Set.of(z));
        assertEquals(GameState.FAILED, level.getGameState());
    }

    @Test
    @DisplayName("Спавн добавляет зомби на поле в ожидаемые клетки по таймингу волн")
    void spawnAddsZombiesToFieldAtCorrectCellsPerTick() throws Exception {
        level.gameStart();
        long base = 1_000_000L;
        spawner.spawn(base);

        Map<Long, List<WaveSlot>> byTick = expectedWaves().stream()
                .collect(Collectors.groupingBy(WaveSlot::spawnTick, TreeMap::new, Collectors.toList()));

        int totalSpawned = 0;
        for (Map.Entry<Long, List<WaveSlot>> e : byTick.entrySet()) {
            long spawnSinceStart = e.getKey();
            Set<Zombie> before = new HashSet<>(field.getZombieSnapshot());

            boolean spawned = spawner.spawn(base + spawnSinceStart);
            assertTrue(spawned);

            Set<Zombie> after = new HashSet<>(field.getZombieSnapshot());
            after.removeAll(before);

            totalSpawned += e.getValue().size();
            assertEquals(e.getValue().size(), after.size());

            for (WaveSlot slot : e.getValue()) {
                Cell c = getCellToSpawnZombie(field, slot.line());
                int expectedX = c.getRightBottom().x();
                int expectedY = c.getCenter().y();
                boolean foundAtExpectedCell = after.stream().anyMatch(z ->
                        z.getPosition().x() == expectedX && z.getPosition().y() == expectedY
                );
                assertTrue(foundAtExpectedCell);
            }
            assertEquals(totalSpawned, field.getZombieSnapshot().size());
        }
    }

    @Test
    @DisplayName("Состояние COMPLETED наступает, если волны закончились и все зомби убиты")
    void completedWhenAllWavesConsumedAndAllZombiesDead() {
        level.gameStart();
        long base = 2_000_000L;
        spawner.spawn(base);
        long maxTick = expectedWaves().stream().mapToLong(WaveSlot::spawnTick).max().orElse(0);
        spawner.spawn(base + maxTick + 1);
        field.getZombies().forEach(Zombie::kill);
        assertEquals(GameState.COMPLETED, level.getGameState());
    }
}
