package vstu.oop.model.entity.mob.plant.impl;

import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.impl.potato_mine.PotatoMine;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public enum PlantInfo {
    PEA_SHOOTER(100, "Горохострел", "Стреляет горохом", PeaShooter.class),
    SUN_FLOWER(50, "Подсолнух", "Вырабатывает солнечную энергию", SunFlower.class),
    POTATO_MINE(80, "Картошка-мина", "Вырастая, способна убить всех в своей клумбе", PotatoMine.class)
    ;

    private final long price;
    private final String name;
    private final String description;
    private final Class<? extends Plant> implementation;

    PlantInfo(long price, String name, String description, Class<? extends Plant> implementation) {
        this.price = price;
        this.name = name;
        this.description = description;
        this.implementation = implementation;
    }

    public long getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Plant> getImplementation() {
        return implementation;
    }

    public String getDescription() {
        return description;
    }

    public static List<PlantInfo> getPlantsInfoByType(Set<Class<? extends Plant>> types) {
        return Arrays.stream(values())
                .filter(t -> types.contains(t.getImplementation()))
                .toList();
    }
}
