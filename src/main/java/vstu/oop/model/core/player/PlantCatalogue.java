package vstu.oop.model.core.player;

import vstu.oop.model.core.level.PlantSpawner;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.impl.PlantInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlantCatalogue {

    private final Map<Class<? extends Plant>, Long> plantsPriceList;
    private final PlantSpawner plantSpawner;

    public PlantCatalogue(
            PlantSpawner plantSpawner
    ) {
        this.plantSpawner = plantSpawner;
        plantsPriceList = PlantInfo.getPlantsInfoByType(plantSpawner.getAllowedPlants()).stream()
                .collect(Collectors.toMap(PlantInfo::getImplementation, PlantInfo::getPrice));
    }

    boolean enoughMoneyToBuy(Class<? extends Plant> plant, SunTokenWallet wallet) {
        return plantsPriceList.getOrDefault(plant, Long.MAX_VALUE) <= wallet.getSunTokens();
    }

    boolean notEnoughMoneyToBuy(Class<? extends Plant> plant, SunTokenWallet wallet) {
        return !enoughMoneyToBuy(plant, wallet);
    }

    public boolean hasPlant(Class<? extends Plant> plant) {
        return plantSpawner.canBeSpawned(plant);
    }

    public boolean hasNotPlant(Class<? extends Plant> plant) {
        return !hasPlant(plant);
    }

    <P extends Plant> P buyPlant(Class<P> plantType, SunTokenWallet wallet) {
        if (notEnoughMoneyToBuy(plantType, wallet)) {
            throw new IllegalArgumentException("Not enough money to buy");
        }

        P plant = plantSpawner.spawn(plantType);
        int price = Math.toIntExact(plantsPriceList.get(plantType));
        wallet.deductSunTokens(price);

        return plant;
    }

    public List<PlantInfo> getPlantsInfo() {
        return PlantInfo.getPlantsInfoByType(plantSpawner.getAllowedPlants());
    }
}
