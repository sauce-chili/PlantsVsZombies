package vstu.oop.model.core.level;

import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.ProducingPlantBehavior;
import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static vstu.oop.utils.Util.UNCHECKED;

public class PlantSpawner {

    private final Set<Class<? extends Plant>> allowedPlants;
    private final Field field;
    private final ProducingPlantBehavior.TokenSunConsumer sunConsumer;

    private final Map<Class<? extends Plant>, Supplier<? extends Plant>> provider = Map.of(
            PeaShooter.class, this::providePeaShooter,
            SunFlower.class, this::provideSunFlower,
            PotatoMine.class, this::providePotatoMine
    );

    public PlantSpawner(
            Field field,
            ProducingPlantBehavior.TokenSunConsumer sunConsumer,
            Set<Class<? extends Plant>> allowedPlant
    ) {
        this.field = field;
        this.allowedPlants = allowedPlant;
        this.sunConsumer = sunConsumer;
    }

    @SuppressWarnings(UNCHECKED)
    public <P extends Plant> P spawn(Class<P> plant) {

        if (!canBeSpawned(plant)) {
            throw new IllegalArgumentException("Plant is not allowed!");
        }

        if (!provider.containsKey(plant)) {
            throw new IllegalArgumentException("Plant is not registered!");
        }

        return (P) provider.get(plant).get();
    }

    public boolean canBeSpawned(Class<? extends Plant> plant) {
        return allowedPlants.contains(plant);
    }

    public Set<Class<? extends Plant>> getAllowedPlants() {
        return Collections.unmodifiableSet(allowedPlants);
    }

    private PeaShooter providePeaShooter() {
        return new PeaShooter(field);
    }

    private SunFlower provideSunFlower() {
        return new SunFlower(sunConsumer);
    }

    private PotatoMine providePotatoMine() {
        return new PotatoMine(field);
    }
}
