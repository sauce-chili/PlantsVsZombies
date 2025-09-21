package vstu.oop.model.core.player;

import vstu.oop.model.core.world.Cell;
import vstu.oop.model.entity.mob.plant.Plant;

import java.util.function.BiConsumer;

public interface PlayerListener {
    void onPlantPlaced(Plant plant, Cell place);

    void onPlayerSunTokenBalanceChanged(int prevBalance, int newBalance);

    // P.S: лень было разносить по разным интерфейсам, знаю что нарушает ISP

    static PlayerListener onlyPlantPlacedLister(BiConsumer<Plant, Cell> consumer) {
        return new PlayerListener() {
            @Override
            public void onPlantPlaced(Plant plant, Cell place) {
                consumer.accept(plant, place);
            }

            @Override
            public void onPlayerSunTokenBalanceChanged(int prevBalance, int newBalance) {
            }
        };
    }

    static PlayerListener onlyPlayerSunTokenBalanceChangedLister(BiConsumer<Integer, Integer> consumer) {
        return new PlayerListener() {
            @Override
            public void onPlayerSunTokenBalanceChanged(int prevBalance, int newBalance) {
                consumer.accept(prevBalance, newBalance);
            }

            @Override
            public void onPlantPlaced(Plant plant, Cell place) {
            }
        };
    }
}
