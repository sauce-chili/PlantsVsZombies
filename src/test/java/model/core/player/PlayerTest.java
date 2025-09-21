package model.core.player;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vstu.oop.model.core.level.PlantSpawner;
import vstu.oop.model.core.player.PlantCatalogue;
import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.player.PlayerListener;
import vstu.oop.model.core.world.*;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.PlantInfo;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static test_util.TestFieldUtil.createDefaultField;

public class PlayerTest {

    private Player player;
    private PlantSpawner spawner;
    private PlantCatalogue catalogue;
    private Field field;
    private Cell emptyCell;

    @BeforeEach
    void setUp() {
        player = new Player(200);
        field = createDefaultField();
        emptyCell = field.getLine(0)[1];

        spawner = new PlantSpawner(field, player.getSunTokenSunConsumer(), Set.of(SunFlower.class, PeaShooter.class));
        catalogue = new PlantCatalogue(spawner);
        player.setCatalogue(catalogue);
    }

    @AfterEach
    void tearDown() {
        field.clear();
        player.clearSelection();
    }

    // --- setSelectedPlant ---

    @Test
    @DisplayName("setSelectedPlant возвращает false и не устанавливает растение, если его нет в каталоге")
    void setSelectedPlantFailsIfNotInCatalogue() {
        boolean result = player.setSelectedPlant(PotatoMine.class);
        assertFalse(result);
        assertTrue(player.getSelectedPlantToBuy().isEmpty());
    }

    @Test
    @DisplayName("setSelectedPlant возвращает false и не устанавливает растение, если не хватает средств")
    void setSelectedPlantFailsIfNotEnoughFunds() {
        Player poor = new Player(0);
        poor.setCatalogue(catalogue);

        boolean result = poor.setSelectedPlant(SunFlower.class);
        assertFalse(result);
        assertTrue(poor.getSelectedPlantToBuy().isEmpty());
    }

    @Test
    @DisplayName("setSelectedPlant: успех -> неуспех -> успех у одного игрока")
    void setSelectedPlantSuccessFailSuccess() {
        // Шаг 1: успешный выбор SunFlower
        boolean first = player.setSelectedPlant(SunFlower.class);
        assertTrue(first);
        assertEquals(SunFlower.class, player.getSelectedPlantToBuy().orElseThrow(),
                "После успешного выбора должно быть установлено SunFlower");

        // Шаг 2: неуспешный выбор PotatoMine (не в каталоге)
        boolean second = player.setSelectedPlant(PotatoMine.class);
        assertFalse(second);
        assertEquals(SunFlower.class, player.getSelectedPlantToBuy().orElseThrow(),
                "После неуспешного выбора должно остаться SunFlower");

        // Шаг 3: снова успешный выбор PeaShooter
        boolean third = player.setSelectedPlant(PeaShooter.class);
        assertTrue(third);
        assertEquals(PeaShooter.class, player.getSelectedPlantToBuy().orElseThrow(),
                "После повторного успешного выбора должно быть установлено PeaShooter");
    }

    // --- canBeBought ---

    @Test
    @DisplayName("canBeBought true если хватает денег и растение есть в каталоге")
    void canBeBoughtTrueIfEnoughFundsAndInCatalogue() {
        assertTrue(player.canBeBought(SunFlower.class));
    }

    @Test
    @DisplayName("canBeBought false если не хватает денег")
    void canBeBoughtFalseIfNotEnoughFunds() {
        Player poor = new Player(0);
        poor.setCatalogue(catalogue);
        assertFalse(poor.canBeBought(SunFlower.class));
    }

    @Test
    @DisplayName("canBeBought false если растение отсутствует в каталоге")
    void canBeBoughtFalseIfPlantNotInCatalogue() {
        assertFalse(player.canBeBought(PotatoMine.class));
    }

    // --- getCatalogue ---

    @Test
    @DisplayName("getCatalogue возвращает именно тот каталог, который был установлен")
    void getCatalogueReturnsSameCatalogue() {
        PlantCatalogue cat = player.getCatalogue();
        assertSame(catalogue, cat);

        // Ожидаемый набор растений — тот, что в spawner
        Set<Class<? extends Plant>> expected = spawner.getAllowedPlants();

        // Фактический набор растений из каталога
        Set<Class<? extends Plant>> actual = cat.getPlantsInfo().stream()
                .map(PlantInfo::getImplementation)
                .collect(Collectors.toSet());

        assertEquals(expected, actual, () -> "Содержимое каталога не совпадает: " +
                "expected=" + expected + ", actual=" + actual);

        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }

    // --- setCatalogue ---

    @Test
    @DisplayName("setCatalogue можно вызвать только один раз")
    void setCatalogueCanBeCalledOnlyOnce() {
        assertThrows(IllegalStateException.class, () -> player.setCatalogue(new PlantCatalogue(spawner)));
    }

    // --- методы PlantCatalogue: hasPlant / hasNotPlant ---

    @Test
    @DisplayName("hasPlant возвращает true для разрешённого растения")
    void hasPlantReturnsTrueForAllowedPlant() {
        assertTrue(catalogue.hasPlant(SunFlower.class));
    }

    @Test
    @DisplayName("hasNotPlant возвращает true для неразрешённого растения")
    void hasNotPlantReturnsTrueForNotAllowedPlant() {
        assertTrue(catalogue.hasNotPlant(PotatoMine.class));
    }

    @Test
    @DisplayName("hasPlant false и hasNotPlant true для растения вне каталога")
    void hasPlantFalseForUnknownPlant() {
        assertFalse(catalogue.hasPlant(PotatoMine.class));
        assertTrue(catalogue.hasNotPlant(PotatoMine.class));
    }

    // -------- clearSelection --------

    @Test
    @DisplayName("setSelectedPlant выбирает растение, clearSelection сбрасывает выбор")
    void setAndClearSelection() {
        player.setSelectedPlant(SunFlower.class);
        assertEquals(SunFlower.class, player.getSelectedPlantToBuy().orElseThrow());

        player.clearSelection();
        assertTrue(player.getSelectedPlantToBuy().isEmpty());
    }

    @Test
    @DisplayName("clearSelection можно вызвать несколько раз подряд")
    void clearSelectionIdempotent() {
        player.clearSelection(); // без выбора
        assertTrue(player.getSelectedPlantToBuy().isEmpty());

        player.setSelectedPlant(SunFlower.class);
        player.clearSelection();
        player.clearSelection(); // повторный сброс
        assertTrue(player.getSelectedPlantToBuy().isEmpty());
    }

    // -------- placeSelectedPlant --------

    @Test
    @DisplayName("placeSelectedPlant успешно садит растение и списывает стоимость")
    void placeSelectedPlantPlacesPlantAndDeductsPrice() {
        player.setSelectedPlant(SunFlower.class);
        int balanceBefore = player.getBalance();

        player.placeSelectedPlant(emptyCell);

        Plant planted = emptyCell.getPlant().orElseThrow();
        assertInstanceOf(SunFlower.class, planted);

        int balanceAfter = player.getBalance();
        long expectedPrice = player.getCatalogue().getPlantsInfo().stream()
                .filter(info -> info.getImplementation().equals(SunFlower.class))
                .findFirst().orElseThrow().getPrice();

        long expectedBalanceAfter = balanceBefore - expectedPrice;

        assertEquals(expectedBalanceAfter, balanceAfter);
    }

    @Test
    @DisplayName("placeSelectedPlant позволяет посадить несколько растений в разные клетки с корректным списанием стоимости")
    void placeSelectedPlantMultiplePlants() {
        // вторая клетка в той же строке
        Cell anotherCell = field.getLine(0)[2];

        int balanceBefore = player.getBalance();

        // Шаг 1: посадка SunFlower
        player.setSelectedPlant(SunFlower.class);
        player.placeSelectedPlant(emptyCell);
        Plant planted1 = emptyCell.getPlant().orElseThrow();
        assertInstanceOf(SunFlower.class, planted1);

        int balanceAfterFirst = player.getBalance();
        long price1 = player.getCatalogue().getPlantsInfo().stream()
                .filter(info -> info.getImplementation().equals(SunFlower.class))
                .findFirst().orElseThrow().getPrice();
        assertEquals(balanceBefore - price1, balanceAfterFirst,
                "Баланс должен уменьшиться ровно на цену SunFlower");

        // Шаг 2: посадка PeaShooter
        player.setSelectedPlant(PeaShooter.class);
        player.placeSelectedPlant(anotherCell);
        Plant planted2 = anotherCell.getPlant().orElseThrow();
        assertInstanceOf(PeaShooter.class, planted2);

        int balanceAfterSecond = player.getBalance();
        long price2 = player.getCatalogue().getPlantsInfo().stream()
                .filter(info -> info.getImplementation().equals(PeaShooter.class))
                .findFirst().orElseThrow().getPrice();
        assertEquals(balanceAfterFirst - price2, balanceAfterSecond,
                "Баланс должен уменьшиться ровно на цену PeaShooter");

        // финальная проверка: в обеих клетках сидят растения
        assertInstanceOf(SunFlower.class, emptyCell.getPlant().orElseThrow());
        assertInstanceOf(PeaShooter.class, anotherCell.getPlant().orElseThrow());
    }


    @Test
    @DisplayName("placeSelectedPlant возвращает исключение, если не хватает средств")
    void placeSelectedPlantThrowsIfNotEnoughFunds() {
        Player poor = new Player(0);
        poor.setCatalogue(catalogue);
        poor.setSelectedPlant(SunFlower.class);

        assertEquals(IllegalStateException.class, poor.placeSelectedPlant(emptyCell).get().getClass());
        assertTrue(emptyCell.getPlant().isEmpty());
    }

    @Test
    @DisplayName("placeSelectedPlant возвращает исключение, если растение не разрешено")
    void placeSelectedPlantThrowsIfPlantNotAllowed() {
        player.setSelectedPlant(PotatoMine.class); // не в allowedPlants
        assertEquals(IllegalStateException.class, player.placeSelectedPlant(emptyCell).get().getClass());
        assertTrue(emptyCell.getPlant().isEmpty());
    }

    @Test
    @DisplayName("placeSelectedPlant возвращает исключение, если выбор растения не сделан")
    void placeSelectedPlantThrowsIfNoSelection() {
        assertEquals(IllegalStateException.class, player.placeSelectedPlant(emptyCell).get().getClass());
        assertTrue(emptyCell.getPlant().isEmpty());
    }

    @Test
    @DisplayName("placeSelectedPlant возвращает исключение, если клетка занята")
    void placeSelectedPlantThrowsIfCellOccupied() {
        player.setSelectedPlant(SunFlower.class);
        emptyCell.setPlant(new SunFlower(amount -> {
        }));

        assertEquals(IllegalArgumentException.class, player.placeSelectedPlant(emptyCell).get().getClass());
    }

    // -------- Listeners --------

    @Test
    @DisplayName("subscribe и unsubscribe работают корректно")
    void subscribeAndUnsubscribe() {
        PlayerListener listener = mock(PlayerListener.class);
        player.subscribe(listener);
        player.unsubscribe(listener);

        // вызываем событие вручную (через реальный сценарий)
        player.setSelectedPlant(SunFlower.class);
        player.placeSelectedPlant(emptyCell);

        // после отписки не должно быть вызовов
        verifyNoInteractions(listener);
    }

    @Test
    @DisplayName("fireSunTokenBalanceChanged уведомляет подписчиков")
    void fireSunTokenBalanceChangedNotifiesListeners() {
        AtomicInteger prev = new AtomicInteger();
        AtomicInteger curr = new AtomicInteger();
        PlayerListener listener = PlayerListener.onlyPlayerSunTokenBalanceChangedLister(
                (prevBalance, newBalance) -> {
                    prev.set(prevBalance);
                    curr.set(newBalance);
                }
        );
        player.subscribe(listener);

        player.setSelectedPlant(SunFlower.class);
        player.placeSelectedPlant(emptyCell);

        assertTrue(prev.get() > curr.get());
    }

    @Test
    @DisplayName("firePlantPlaced уведомляет подписчиков о посадке растения")
    void firePlantPlacedNotifiesListeners() {
        AtomicBoolean called = new AtomicBoolean(false);
        PlayerListener listener = PlayerListener.onlyPlantPlacedLister((Plant plant, Cell cell) -> {
            called.set(true);
            assertInstanceOf(SunFlower.class, plant);
            assertEquals(emptyCell, cell);
        });

        player.subscribe(listener);

        player.setSelectedPlant(SunFlower.class);
        player.placeSelectedPlant(emptyCell);

        assertTrue(called.get(), "Listener должен быть вызван при посадке растения");
    }
}
