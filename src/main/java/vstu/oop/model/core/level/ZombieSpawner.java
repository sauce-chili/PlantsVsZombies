package vstu.oop.model.core.level;

import vstu.oop.model.core.world.Field;
import vstu.oop.model.core.world.Position;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;
import vstu.oop.model.entity.mob.zombie.Zombie;
import vstu.oop.utils.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static vstu.oop.utils.Util.UNCHECKED;

public class ZombieSpawner {

    private final Map<Class<? extends Zombie>, Function<Position, ? extends Zombie>> providers = Map.of(
            SimpleZombie.class, this::provideSimpleZombie
    );

    private final Field field;
    private final List<WaveSlot> waves;

    private Long tickOfFirstSpawnRequest;

    public ZombieSpawner(
            List<WaveSlot> waves,
            Field field
    ) {
        this.field = field;
        this.waves = new ArrayList<>(waves);
    }

    public boolean spawn(long tick) {
        if (tickOfFirstSpawnRequest == null) {
            tickOfFirstSpawnRequest = tick;
        }

        long tickSinceGameStart = tick - tickOfFirstSpawnRequest;

        Set<WaveSlot> slotToSpawn = waves.stream()
                .filter(slot -> tickSinceGameStart >= slot.spawnTick())
                .collect(Collectors.toSet());

        if (slotToSpawn.isEmpty()) {
            return false;
        }

        Set<Pair<Integer, Function<Position, Zombie>>> zombiesInLineSpawner = slotToSpawn.stream()
                .map(slot -> {
                    Function<Position, Zombie> spawner = p -> spawn(slot.zombie(), p);
                    return new Pair<>(slot.line(), spawner);
                })
                .collect(Collectors.toSet());

        waves.removeAll(slotToSpawn);
        field.addZombies(zombiesInLineSpawner);

        return true;
    }

    public boolean allZombieSpawned() {
        return waves.isEmpty();
    }

    public boolean hasNotSpawnedWaves() {
        return !allZombieSpawned();
    }

    @SuppressWarnings(UNCHECKED)
    private <Z extends Zombie> Z spawn(Class<Z> zombie, Position pos) {
        return (Z) providers.get(zombie).apply(pos);
    }

    private SimpleZombie provideSimpleZombie(Position pos) {
        return new SimpleZombie(pos, field);
    }
}
