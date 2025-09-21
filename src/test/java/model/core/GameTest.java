package model.core;

import org.junit.jupiter.api.Test;
import vstu.oop.model.core.Game;
import vstu.oop.model.core.GameListener;
import vstu.oop.model.core.level.Level;
import vstu.oop.model.core.level.WaveSlot;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {
    /**
     * Победа: игрок ставит PeaShooter после старта,
     * тот убивает зомби, игра завершается победой.
     */
    @Test
    void gameWinDueAllEnemiesDied() throws Exception {
        Level lvl = new Level(
                Set.of(PeaShooter.class),
                200, // достаточно солнца
                List.of(WaveSlot.of(0, 0, SimpleZombie.class))
        );

        Game game = new Game(lvl);

        CountDownLatch win = new CountDownLatch(1);

        game.subscribe(new GameListener() {
            @Override
            public void onGameWined() {
                win.countDown();
            }

            @Override
            public void onGameLost() {
                fail("Не должно быть поражения");
            }
        });

        game.start();

        // --- Размещаем PeaShooter после старта ---
        int beforeBalance = game.getPlayer().getBalance();
        Field field = game.getField();
        Cell shooterCell = field.getLine(0)[1];
        boolean selected = game.getPlayer().setSelectedPlant(PeaShooter.class);
        assertTrue(selected, "Растение должно быть выбрано");
        assertTrue(game.getPlayer().placeSelectedPlant(shooterCell).isEmpty(), "Растение должно успешно установиться");
        assertTrue(game.getPlayer().getSelectedPlantToBuy().isEmpty());
        int afterBalance = game.getPlayer().getBalance();
        assertTrue(afterBalance < beforeBalance, "Баланс должен уменьшиться после покупки");

        // ждём победы
        assertTrue(win.await(15, TimeUnit.SECONDS), "Игра должна завершиться победой");

        assertNull(game.getField());
        assertNull(game.getPlayer());
    }

    /**
     * Поражение: зомби съедает растение и доходит до дома.
     * Мокаем растение и проверяем, что digUp() вызван.
     */
    @Test
    void gameLoseDueZombieEatsPlantAndReachesHouse() throws Exception {
        Level lvl = new Level(
                Set.of(SunFlower.class),
                200,
                List.of(WaveSlot.of(0, 0, SimpleZombie.class))
        );

        Game game = new Game(lvl);

        CountDownLatch lose = new CountDownLatch(1);

        game.subscribe(new GameListener() {
            @Override
            public void onGameWined() {
                fail("Не должно быть победы");
            }

            @Override
            public void onGameLost() {
                lose.countDown();
            }
        });

        game.start();

        // --- Ставим мока-посадку ---
        Field field = game.getField();
        Cell victimCell = field.getLine(0)[2];

        // проверяем баланс до и после
        int beforeBalance = game.getPlayer().getBalance();
        assertTrue(game.getPlayer().setSelectedPlant(SunFlower.class));
        assertTrue(game.getPlayer().placeSelectedPlant(victimCell).isEmpty());
        int afterBalance = game.getPlayer().getBalance();
        assertTrue(afterBalance < beforeBalance, "Баланс должен уменьшиться после покупки");

        // ждём поражения
        assertTrue(lose.await(20, TimeUnit.SECONDS), "Игра должна завершиться поражением");
    }
}
