package vstu.oop.view;

import vstu.oop.model.core.player.PlantCatalogue;
import vstu.oop.model.core.player.Player;
import vstu.oop.model.core.player.PlayerListener;
import vstu.oop.model.core.world.Cell;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.impl.PlantInfo;
import vstu.oop.view.component.PlantCardComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import static vstu.oop.utils.Constance.CATALOGUE_HEIGHT;
import static vstu.oop.utils.Constance.CATALOGUE_WIDTH;
import static vstu.oop.utils.Util.loadByPath;

public class PlantCatalogueView extends JPanel {

    private final Player player;
    private final PlantCatalogue plantCatalogue;

    private final JPanel cardsPanel;
    private final JLabel sunCounter;

    public PlantCatalogueView(Player player) {
        this.player = player;
        this.plantCatalogue = player.getCatalogue();

        setLayout(null); // Absolute positioning
        setPreferredSize(new Dimension(CATALOGUE_WIDTH, CATALOGUE_HEIGHT));
        setBackground(new Color(103, 46, 16));

        // Счетчик солнца
        sunCounter = createSunCounter();
        sunCounter.setBounds(0, 0, 90, CATALOGUE_HEIGHT);
        add(sunCounter);

        // Панель карточек
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
        cardsPanel.setOpaque(false);

        int cardsPanelX = 95; // отступ после счетчика
        int cardsPanelWidth = CATALOGUE_WIDTH - cardsPanelX;
        cardsPanel.setBounds(cardsPanelX, 0, cardsPanelWidth, CATALOGUE_HEIGHT);
        add(cardsPanel);

        // Инициализация карточек растений
        initPlantCards();

        // подписка на действия игрока
        player.subscribe(new CataloguePlayerListener());

        // Начальное обновление состояния
        onSunTokenChanged(player.getBalance());
    }

    private JLabel createSunCounter() {
        return new JLabel("0", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                BufferedImage sunCounterIcon = loadByPath("src/main/resources/component/suntoken_counter.png");

                if (sunCounterIcon != null) {
                    g.drawImage(sunCounterIcon, 0, 0, getWidth(), getHeight(), this);
                }

                String text = getText();
                g.setFont(new Font("Arial", Font.PLAIN, 22));
                g.setColor(Color.BLACK);

                FontMetrics fm = g.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = getHeight() - 7;

                g.drawString(text, textX, textY);
            }
        };
    }

    private void initPlantCards() {
        List<PlantInfo> plantsInfo = plantCatalogue.getPlantsInfo();
        int maxCards = Math.min(plantsInfo.size(), 8);

        for (int i = 0; i < maxCards; i++) {
            PlantInfo info = plantsInfo.get(i);
            PlantCardComponent card = new PlantCardComponent(info);

            card.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    onPlantCardClicked(card);
                }
            });

            cardsPanel.add(card);
        }

        updateCardsAffordability();
    }

    private void onPlantCardClicked(PlantCardComponent card) {
        Optional<Class<? extends Plant>> selectedPlant = player.getSelectedPlantToBuy();

        boolean requiredClearSelection = selectedPlant.isPresent()
                && selectedPlant.get() == card.getPlantInfo().getImplementation();

        if (requiredClearSelection) {
            player.clearSelection();
        } else {
            player.setSelectedPlant(card.getPlantInfo().getImplementation());
        }

        updateCardsSelection();
    }

    private void onSunTokenChanged(int newBalance) {
        updateSunTokenCounter(newBalance);
        updateCardsAffordability();
        updateCardsSelection();
    }

    private void updateSunTokenCounter(int newBalance) {
        sunCounter.setText(String.valueOf(newBalance));
        sunCounter.repaint(); // Принудительная перерисовка
    }

    private void updateCardsAffordability() {
        for (Component c : cardsPanel.getComponents()) {
            if (c instanceof PlantCardComponent card) {
                boolean affordable = player.canBeBought(card.getPlantInfo().getImplementation());
                card.setAffordable(affordable);
            }
        }
    }

    private void updateCardsSelection() {
        Optional<Class<? extends Plant>> selectedPlant = player.getSelectedPlantToBuy();

        for (Component c : cardsPanel.getComponents()) {
            if (c instanceof PlantCardComponent card) {
                boolean isSelected = selectedPlant.isPresent() &&
                        selectedPlant.get() == card.getPlantInfo().getImplementation();
                card.setSelected(isSelected);
            }
        }
    }

    public void freeze() {
        for(Component c : cardsPanel.getComponents()) {
            if (c instanceof PlantCardComponent card) {
                card.setSelected(false);
                card.setAffordable(false);
            }
        }
    }

    private final class CataloguePlayerListener implements PlayerListener {
        @Override
        public void onPlantPlaced(Plant plant, Cell place) {
            updateCardsSelection();
        }

        @Override
        public void onPlayerSunTokenBalanceChanged(int prevBalance, int newBalance) {
            onSunTokenChanged(newBalance);
        }
    }
}
