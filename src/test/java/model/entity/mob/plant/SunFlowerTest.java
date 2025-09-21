package model.entity.mob.plant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SunFlowerTest extends AbstractPlantTest<SunFlower>{
    private Player player;

    @Override
    protected SunFlower createPlant() {
        player = new Player(0); // создаём игрока с 0 очков
        return new SunFlower(player.getSunTokenSunConsumer());
    }

    @Test
    @DisplayName("SunFlower не генерирует солнце сразу после посадки")
    void doesNotProduceImmediately() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        long tick = System.currentTimeMillis();
        plant.act(tick);

        assertEquals(0, player.getBalance(),
                "Сразу после посадки токены не должны появиться");
    }

    @Test
    @DisplayName("SunFlower генерирует 30 токенов после таймаута")
    void producesAfterTimeout() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        long plantedTick = System.currentTimeMillis();

        // прошло 25 секунд (таймаут генерации)
        plant.act(plantedTick );
        plant.act(plantedTick + 100);

        assertEquals(30, player.getBalance(),
                "После таймаута должно появиться 25 токенов");
    }

    @Test
    @DisplayName("SunFlower генерирует токены периодически")
    void producesPeriodically() {
        Cell cell = anyPlantableCell();
        cell.setPlant(plant);

        long plantedTick = System.currentTimeMillis();
        plant.act(plantedTick);
        plant.act(plantedTick + 100);
        assertEquals(30, player.getBalance());

        plant.act(plantedTick + 100);
        assertEquals(60, player.getBalance());

        plant.act(plantedTick + 100);
        assertEquals(90, player.getBalance());
    }
}
