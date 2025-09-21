package vstu.oop.view;

import vstu.oop.model.core.Game;
import vstu.oop.model.core.GameListener;
import vstu.oop.model.core.level.Level;
import vstu.oop.model.core.level.LevelRegistry;
import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.world.Field;

import javax.swing.*;

import java.awt.*;
import java.util.Map;
import java.util.function.Supplier;

import static vstu.oop.utils.Constance.*;

public class GameView extends JFrame {

    private final Game game;

    private FieldView fieldView;
    private PlantCatalogueView catalogueView;
    private JLabel levelLabel;

    public GameView(Game game) {
        this.game = game;
        initUI();
        startViewSubscriptions();
        game.start();
    }

    private void initUI() {
        setTitle("Plants vs Zombies");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
        getContentPane().setPreferredSize(new Dimension(APP_WIDTH, APP_HEIGHT));

        setJMenuBar(buildMenuBar());
        buildViews(game.getField(), game.getPlayer());
        setLevelName(game.getLevel().getLevelName());

        pack();
        setVisible(true);
    }


    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu gameMenu = new JMenu("Игра");

        JMenuItem restart = new JMenuItem("Перезапустить уровень");
        restart.addActionListener(e -> restartLevel());

        JMenuItem select = new JMenuItem("Выбрать уровень…");
        select.addActionListener(e -> selectAndLoadLevel());

        JMenuItem exit = new JMenuItem("Выход");
        exit.addActionListener(e -> exit());

        gameMenu.add(restart);
        gameMenu.add(select);
        gameMenu.addSeparator();
        gameMenu.add(exit);

        bar.add(gameMenu);

        bar.add(Box.createHorizontalStrut(10));

        levelLabel = new JLabel("Уровень: ?");
        levelLabel.setForeground(Color.BLACK);
        bar.add(levelLabel);

        return bar;
    }

    private void setLevelName(String lvlName) {
        levelLabel.setText(String.format("Уровень: %s", lvlName));
    }

    private void buildViews(Field field, Player player) {
        if (catalogueView != null) remove(catalogueView);
        if (fieldView != null) remove(fieldView);

        catalogueView = new PlantCatalogueView(player);
        catalogueView.setBounds(0, 0, CATALOGUE_WIDTH, CATALOGUE_HEIGHT);

        fieldView = new FieldView(field, player);
        fieldView.setBounds(0, CATALOGUE_HEIGHT, FIELD_WIDTH, FIELD_HEIGHT);

        add(catalogueView);
        add(fieldView);

        revalidate();
        repaint();
    }

    /**
     * Быстрый рестарт текущего выбранного уровня
     */
    private void restartLevel() {
        loadLevel(game.getLevel().clone());
    }

    /**
     * Загружает произвольный уровень в существующий Game и перестраивает UI
     */
    private void loadLevel(Level level) {
        // Остановить предыдущее визуальное обновление и подписки
        stopViewSubscriptions();

        // Переключить уровень внутри существующего Game
        game.loadLevel(level);

        // Перестроить панели поверх новых ссылок (Field/Player)
        buildViews(game.getField(), game.getPlayer());
        setLevelName(level.getLevelName());

        // Подписаться и запустить
        startViewSubscriptions();
        game.start();
    }

    public void exit() {
        game.exit();
        catalogueView.freeze();
        fieldView.freeze();
        stopViewSubscriptions();
        dispose();
    }

    /**
     * Диалог выбора уровня по реестру и загрузка
     */
    private void selectAndLoadLevel() {
        Map<String, Supplier<Level>> levels = LevelRegistry.all();
        String lvlName = (String) JOptionPane.showInputDialog(
                this,
                "Выбери уровень:",
                "Выбор уровня",
                JOptionPane.PLAIN_MESSAGE,
                null,
                levels.keySet().toArray(),
                levels.keySet().stream().findFirst().orElse(null)
        );
        if (lvlName != null) {
            loadLevel(levels.get(lvlName).get());
        }
    }

    /**
     * Подписки/запуск визуального обновления
     */
    private void startViewSubscriptions() {
        game.subscribe(GAME_LISTENER);
        fieldView.startRefreshing();
    }

    /**
     * Отписки/остановка визуального обновления
     */
    private void stopViewSubscriptions() {
        if (fieldView != null) {
            fieldView.stopRefreshing();
        }
        game.unsubscribe(GAME_LISTENER);
    }

    /**
     * === Реакции на конец игры: диалог с действием игрока ===
     */
    private final GameListener GAME_LISTENER = new GameListener() {
        @Override
        public void onGameWined() {
            onGameEnded("Зомби повержены! Что дальше?");
        }

        @Override
        public void onGameLost() {
            onGameEnded("Твои мозги съели… Что дальше?");
        }

        private void onGameEnded(String msg) {
            catalogueView.freeze();
            fieldView.freeze();
            stopViewSubscriptions();
            String[] options = {"Перезапустить", "Выбрать уровень", "Отмена"};
            int choice = JOptionPane.showOptionDialog(
                    GameView.this, msg, "Игра окончена",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]
            );
            if (choice == 0) {              // Перезапустить
                restartLevel();
            } else if (choice == 1) {       // Выбрать уровень
                selectAndLoadLevel();
            } else {
                // ничего
            }
        }
    };
}


