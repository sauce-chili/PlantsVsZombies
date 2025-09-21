package vstu.oop.model.entity.mob.damage;

import vstu.oop.model.entity.mob.Mob;

import java.util.function.Consumer;

public record MeleeDamage(
        Mob emitter,
        Long amountHealthDamage
) implements Damage {
}
