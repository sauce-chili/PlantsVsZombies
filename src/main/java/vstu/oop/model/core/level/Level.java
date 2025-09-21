package vstu.oop.model.core.level;

import vstu.oop.model.core.GameState;
import vstu.oop.model.core.player.PlantCatalogue;
import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.world.*;
import vstu.oop.model.entity.mob.plant.Plant;

import java.util.*;

import static java.util.Objects.requireNonNullElse;
import static vstu.oop.utils.Constance.defaultFieldCharacteristics;

public class Level implements Cloneable {

    private Field field;
    private Player player;
    private ZombieSpawner zombieSpawner;

    private Set<Class<? extends Plant>> plantsOnLevel;
    private int initialSunTokenOnLevel;
    private List<WaveSlot> waves;
    private String levelName;

    private boolean gameIsStarted;

    public Level(
            Set<Class<? extends Plant>> plantsOnLevel,
            int initialSunTokenOnLevel,
            List<WaveSlot> waves
    ) {
        this(
                null,
                plantsOnLevel,
                initialSunTokenOnLevel,
                waves

        );
    }

    public Level(
            String levelName,
            Set<Class<? extends Plant>> plantsOnLevel,
            int initialSunTokenOnLevel,
            List<WaveSlot> waves
    ) {
        this.plantsOnLevel = new HashSet<>(plantsOnLevel);
        this.initialSunTokenOnLevel = initialSunTokenOnLevel;
        this.waves = new ArrayList<>(waves);
        this.levelName = requireNonNullElse(levelName, getClass().getSimpleName());

        init();
    }

    private void init() {
        this.field = initField();
        this.zombieSpawner = initZombieSpawner();
        this.player = initPlayer();
    }

    private Field initField() {
        FieldCharacteristics fc = defaultFieldCharacteristics;
        Cell[][] cells = new Cell[fc.rowsOfCell()][fc.columnsOfCell()];

        for (int row = 0; row < fc.rowsOfCell(); ++row) {
            for (int col = 0; col < fc.columnsOfCell(); ++col) {
                int x = fc.plantableCellsStartingWithX() + (col - 1) * fc.cellWidth();
                int y = fc.plantableCellsStartingWithY() + row * fc.cellHeight();
                Position ltp = new Position(x, y);
                Cell cell;
                if (col == 0 || col == fc.columnsOfCell() - 1) {
                    cell = new DisabledCell(ltp, fc.cellWidth(), fc.cellHeight());
                } else {
                    cell = new Cell(ltp, fc.cellWidth(), fc.cellHeight());
                }
                cells[row][col] = cell;
            }
        }
        return new Field(fc, cells);
    }

    private Player initPlayer() {
        Player p = new Player(initialSunTokenOnLevel);
        p.setCatalogue(new PlantCatalogue(new PlantSpawner(
                field,
                p.getSunTokenSunConsumer(),
                plantsOnLevel
        )));
        return p;
    }

    private ZombieSpawner initZombieSpawner() {
        return new ZombieSpawner(waves, field);
    }

    public Field provideField() {
        return field;
    }

    public Player providePlayer() {
        return player;
    }

    public ZombieSpawner provideZombieSpawner() {
        return zombieSpawner;
    }

    public void gameStart() {
        gameIsStarted = true;
    }

    public void gameStop() {
        gameIsStarted = false;
    }

    public GameState getGameState() {
        if (!gameIsStarted) {
            return GameState.INITIAL;
        }

        boolean lvlFailed = field.anyZombieReachedHouse();

        if (lvlFailed) {
            return GameState.FAILED;
        }

        boolean wavesEnded = zombieSpawner.allZombieSpawned();
        boolean allZombieDied = field.allZombieDied();
        boolean lvlCompeted = wavesEnded && allZombieDied;

        if (lvlCompeted) {
            return GameState.COMPLETED;
        }

        return GameState.IN_PROGRESS;
    }

    public String getLevelName() {
        return levelName;
    }

    @Override
    public Level clone() {
        try {
            Level clone = (Level) super.clone();
            clone.waves = new ArrayList<>(this.waves);
            clone.initialSunTokenOnLevel = this.initialSunTokenOnLevel;
            clone.plantsOnLevel = new HashSet<>(this.plantsOnLevel);
            clone.levelName = this.levelName;
            clone.init();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
