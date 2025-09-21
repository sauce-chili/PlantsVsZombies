package vstu.oop.model.core;

import vstu.oop.model.core.level.Level;
import vstu.oop.model.core.level.ZombieSpawner;
import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.world.Field;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class Game {

    private Level lvl;
    private Field field;
    private Player player;
    private ZombieSpawner zombieSpawner;

    private final Collection<GameListener> listeners = new HashSet<>();

    private Timer updateTimer;
    private static final int UPDATE_RATE = 24;
    private static final int UPDATE_PERIOD_MS = 1000 / UPDATE_RATE;

    public Game(Level level) {
        initByLevel(level);
    }

    private void initByLevel(Level level) {
        this.lvl = level;
        this.field = level.provideField();
        this.player = level.providePlayer();
        this.zombieSpawner = level.provideZombieSpawner();
    }

    public void loadLevel(Level level) {
        stop();
        initByLevel(level);
    }

    public void start() {
        lvl.gameStart();
        updateTimer = new Timer("GameUpdateTimer", true);
        TimerTask gameLoop = new TimerTask() {
            public void run() {
                GameState state = lvl.getGameState();
                if (gameEnded(state)) {
                    finish(state);
                    return;
                }

                long tick = System.currentTimeMillis();

                if (state == GameState.IN_PROGRESS) {
                    zombieSpawner.spawn(tick);
                }
                field.update(tick);
            }
        };
        updateTimer.scheduleAtFixedRate(gameLoop, 0, UPDATE_PERIOD_MS);
    }

    private void finish(GameState reason) {
        stop();
        if (reason == GameState.COMPLETED) {
            fireGameWined();
        } else if (reason == GameState.FAILED) {
            fireGameLost();
        }
    }

    public void exit() {
        stop();
        listeners.clear();
    }

    private void stop() {
        if (updateTimer != null) {
            updateTimer.cancel();
            updateTimer.purge();
            updateTimer = null;
        }
        if (field != null) field.clear();
        if (player != null) player.clearSelection();
        if (lvl != null) lvl.gameStop();

        field = null;
        player = null;
        zombieSpawner = null;
    }

    private boolean gameEnded(GameState state) {
        return state == GameState.COMPLETED || state == GameState.FAILED;
    }

    public Player getPlayer() {
        return player;
    }

    public Field getField() {
        return field;
    }

    public Level getLevel() {
        return lvl;
    }

    public void subscribe(GameListener listener) {
        requireNonNull(listener);
        listeners.add(listener);
    }

    public void unsubscribe(GameListener listener) {
        requireNonNull(listener);
        listeners.remove(listener);
    }

    private void fireGameWined() {
        listeners.forEach(GameListener::onGameWined);
    }

    private void fireGameLost() {
        listeners.forEach(GameListener::onGameLost);
    }
}
