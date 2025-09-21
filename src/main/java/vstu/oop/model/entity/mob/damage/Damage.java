package vstu.oop.model.entity.mob.damage;

import vstu.oop.model.entity.mob.Mob;

import java.util.function.Consumer;

public interface Damage {
    Mob emitter();

    Long amountHealthDamage();
}
