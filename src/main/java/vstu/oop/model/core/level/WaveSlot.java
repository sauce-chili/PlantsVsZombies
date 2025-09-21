package vstu.oop.model.core.level;

import vstu.oop.model.entity.mob.zombie.Zombie;

public record WaveSlot(
        long spawnTick,
        int line,
        Class<? extends Zombie> zombie
) {
    public static WaveSlot of(long spawnTick, int line, Class<? extends Zombie> zombie) {
        return new WaveSlot(spawnTick, line, zombie);
    }
}
