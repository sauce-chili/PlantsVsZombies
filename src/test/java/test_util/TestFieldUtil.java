package test_util;

import vstu.oop.model.core.world.*;
import vstu.oop.model.entity.mob.zombie.Zombie;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static vstu.oop.utils.Constance.defaultFieldCharacteristics;
import static vstu.oop.utils.Util.UNCHECKED;

public final class TestFieldUtil {

    private static final Map<String, Method> cachedFieldMethods = new HashMap<>();

    private static final String getCharacteristics_METHOD_NAME = "getCharacteristics";
    private static final String spawnZombies_METHOD_NAME = "spawnZombies";
    private static final String getCellToSpawnZombie_METHOD_NAME = "getCellToSpawnZombie";

    private static Method getCachedMethod(
            Class<?> clazz,
            String methodName,
            Class<?>... parameterTypes
    ) {
        // ключ = имя класса + имя метода + список типов параметров
        String key = clazz.getName() + "#" + methodName + Arrays.toString(parameterTypes);

        return cachedFieldMethods.computeIfAbsent(key, k -> {
            try {
                Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Method not found: " + key, e);
            }
        });
    }

    @SuppressWarnings(UNCHECKED)
    private static <R> R invokeFieldMethod(Field field, String methodName, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = getCachedMethod(Field.class, methodName, parameterTypes);
            return (R) method.invoke(field, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Field createDefaultField() {
        FieldCharacteristics fc = defaultFieldCharacteristics;
        Cell[][] cells = new Cell[fc.rowsOfCell()][fc.columnsOfCell()];
        for (int row = 0; row < fc.rowsOfCell(); row++) {
            for (int col = 0; col < fc.columnsOfCell(); col++) {
                Position pos = new Position(
                        fc.plantableCellsStartingWithX() + col * fc.cellWidth(),
                        fc.plantableCellsStartingWithY() + row * fc.cellHeight()
                );
                cells[row][col] = (col == 0 || col == fc.columnsOfCell() - 1)
                        ? new DisabledCell(pos, fc.cellWidth(), fc.cellHeight())
                        : new Cell(pos, fc.cellWidth(), fc.cellHeight());
            }
        }
        return new Field(fc, cells);
    }

    /**
     * Вызов приватного метода:
     * {@code private void addZombies(Collection<Zombie> zombies) {...};}
     */
    public static void spawnZombiesInField(Field field, Collection<Zombie> zombies) {
        invokeFieldMethod(field, spawnZombies_METHOD_NAME, new Class[]{Collection.class}, zombies);
    }

    /**
     * Вызов приватного метода:
     * {@code private FieldCharacteristics getCharacteristics() {...}}
     */
    public static FieldCharacteristics getFieldCharacteristics(Field field) {
        return invokeFieldMethod(field, getCharacteristics_METHOD_NAME, new Class[]{});
    }

    /**
     * Вызов приватного метода:
     * {@code private Cell getCellToSpawnZombie(int line) {...}}
     */
    public static Cell getCellToSpawnZombie(Field field, int line) {
        return invokeFieldMethod(field, getCellToSpawnZombie_METHOD_NAME, new Class[]{int.class}, line);
    }

}
