package model.core.level;

import vstu.oop.model.core.level.Level1;
import vstu.oop.model.core.level.WaveSlot;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;

import java.util.List;
import java.util.Set;

public class Level1Test extends AbstractLevelTest<Level1> {
    @Override
    protected Level1 newLevel() {
        return new Level1();
    }

    @Override
    protected Set<Class<? extends Plant>> expectedAllowedPlants() {
        return Set.of(SunFlower.class, PeaShooter.class, PotatoMine.class);
    }

    @Override
    protected int expectedInitialSunTokens() {
        return 130;
    }

    @Override
    protected List<WaveSlot> expectedWaves() {
        return List.of(
                WaveSlot.of(2000, 1, SimpleZombie.class),
                WaveSlot.of(6400, 2, SimpleZombie.class),
                WaveSlot.of(2120, 1, SimpleZombie.class),
                WaveSlot.of(8000, 4, SimpleZombie.class),
                WaveSlot.of(2500, 0, SimpleZombie.class)
        );
    }
}
