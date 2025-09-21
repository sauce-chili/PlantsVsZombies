package vstu.oop.view;

import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.Field;
import vstu.oop.model.entity.GameObject;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.view.component.CellComponent;
import vstu.oop.view.component.ComponentFactory;
import vstu.oop.view.component.game_object.mob.plant.PlantComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Supplier;
import javax.swing.Timer;

import static vstu.oop.utils.Util.loadByPath;

public class FieldView extends JLayeredPane {

    private static final Integer BACKGROUND_LAYER = 100;
    private static final Integer GRID_LAYER = 200;
    private static final Integer PLANT_LAYER = 300;
    private static final Integer ZOMBIE_LAYER = 400;
    private static final Integer PROJECTILE_LAYER = 500;

    // Модели
    private final Player player;
    private final Field field;

    private final static BufferedImage backgroundImg = loadByPath("src/main/resources/background/field_background.png");

    private Timer refreshTimer;

    public FieldView(Field field, Player player) {
        this.field = field;
        this.player = player;

        initUI();
    }

    private void initUI() {
        setLayout(null);
        setOpaque(false);

        initBackground();
        initGrid();
    }

    private void initBackground() {
        setPreferredSize(new Dimension(backgroundImg.getWidth(), backgroundImg.getHeight()));
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImg, 0, 0, this);
            }
        };
        backgroundPanel.setBounds(0, 0, getPreferredSize().width, getPreferredSize().height);
        add(backgroundPanel, BACKGROUND_LAYER);
    }

    private void initGrid() {
        removeComponentInLayer(GRID_LAYER);
        field.getCells().forEach(cell -> {

            CellComponent cellComp = new CellComponent(cell);

            cellComp.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (cellComp.isEnabled()) {
                        onCellClicked(cell);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                        onMouseHovered(cellComp);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                        onMouseUnhovered(cellComp);
                }
            });

            add(cellComp, GRID_LAYER);
        });
    }

    private void onCellClicked(Cell cell) {
        player.getSelectedPlantToBuy().ifPresent(ignored -> {
            Optional<Throwable> result = player.placeSelectedPlant(cell);
            if (result.isPresent()) {
                JOptionPane.showMessageDialog(this, result.get().getMessage());
            } else {
                // если ошибки не вернулось, то cell игроком было установлено растение
                cell.getPlant().ifPresent(this::addPlantComponent);
            }
        });
    }

    private void addPlantComponent(Plant plant) {
        PlantComponent<? extends Plant> plantComp = ComponentFactory.create(plant);
        add(plantComp, PLANT_LAYER);
    }

    private void onMouseHovered(CellComponent cell) {
        player.getSelectedPlantToBuy().ifPresent(ignored -> {
            cell.enableHighlighting();
        });
    }

    private void onMouseUnhovered(CellComponent cell) {
        if (cell.isHighlighted()) {
            cell.disableHighlighting();
        }
    }

    public void startRefreshing() {
        if (refreshTimer != null && refreshTimer.isRunning()) return;

        refreshTimer = new Timer(1000 / 60, e -> refreshDynamicComponents());
        refreshTimer.start();
    }

    public void stopRefreshing() {
        if (refreshTimer != null) {
            refreshDynamicComponents();
            refreshTimer.stop();
            refreshTimer = null;
        }
    }

    private void refreshDynamicComponents() {
        /*
         * Упаси Господь Бог от race condition мою грешную душу
         * Аминь
         * */
        refreshProjectiles();
        refreshPlants();
        refreshZombies();
    }

    private void refreshPlants() {
        refreshDynamicComponents(PLANT_LAYER, field::getPlantSnapshot);
    }

    private void refreshZombies() {
        refreshDynamicComponents(ZOMBIE_LAYER, field::getZombieSnapshot);
    }

    private void refreshProjectiles() {
        refreshDynamicComponents(
                PROJECTILE_LAYER,
                () -> field.getProjectilesContainer().getProjectilesSnapshot()
        );
    }

    // мб репейнт надо производить для самой общей зоны всех объектов, чтобы случайно не скрыть верно отриованный объект
    private <GO extends GameObject> void refreshDynamicComponents(
            Integer layer,
            Supplier<Collection<GO>> componentsProvider
    ) {
        removeComponentInLayer(layer);

        componentsProvider.get().forEach(existsComp -> {
            Component newComp = ComponentFactory.create(existsComp);
            add(newComp, layer);
        });
    }

    private void removeComponentInLayer(int layer) {
        Rectangle dirtyRegion = null;

        for (Component comp : getComponentsInLayer(layer)) {
            Rectangle bounds = comp.getBounds();
            dirtyRegion = (dirtyRegion == null)
                    ? bounds
                    : dirtyRegion.union(bounds);

            remove(comp);
        }

        if (dirtyRegion != null) {
            repaint(dirtyRegion);
        }
    }

    public void freeze() {
        for (Component c : getComponentsInLayer(GRID_LAYER)) {
            if (c instanceof CellComponent cell) {
                cell.setEnabled(false);
            }
        }
    }
}