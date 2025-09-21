package vstu.oop.model.core.level;

import vstu.oop.model.entity.mob.plant.impl.PeaShooter;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;
import vstu.oop.model.entity.mob.plant.impl.SunFlower;
import vstu.oop.model.entity.mob.zombie.SimpleZombie;

import java.util.List;
import java.util.Set;

public class Level1 extends Level {
    public Level1() {
        super(
                Set.of(
                        SunFlower.class,
                        PeaShooter.class,
                        PotatoMine.class
                ),
                130,
                List.of(
                        WaveSlot.of(2000, 1, SimpleZombie.class),
                        WaveSlot.of(6400, 2, SimpleZombie.class),
                        WaveSlot.of(2120, 1, SimpleZombie.class),
                        WaveSlot.of(8000, 4, SimpleZombie.class),
                        WaveSlot.of(2500, 0, SimpleZombie.class)
                )
        );
    }
}
