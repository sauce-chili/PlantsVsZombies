package vstu.oop.model.core.level;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LevelRegistry {
    private static final Map<String, Supplier<Level>> REGISTRY = new LinkedHashMap<>();

    static {
        REGISTRY.put(Level1.class.getSimpleName(), Level1::new);
    }

    public static Map<String, Supplier<Level>> all() {
        return new LinkedHashMap<>(REGISTRY);
    }
}
