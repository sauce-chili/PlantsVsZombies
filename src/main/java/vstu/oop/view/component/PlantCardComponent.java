package vstu.oop.view.component;

import vstu.oop.model.entity.mob.plant.impl.PlantInfo;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.Map;

import static vstu.oop.model.entity.mob.plant.impl.PlantInfo.*;
import static vstu.oop.utils.Util.loadByPath;

public class PlantCardComponent extends Component<PlantInfo> {

    private final static EnumMap<PlantInfo, BufferedImage> iconLoader = new EnumMap<>(Map.of(
            PEA_SHOOTER, loadByPath("src/main/resources/component/plant_card_icon/card_peashooter.png"),
            SUN_FLOWER, loadByPath("src/main/resources/component/plant_card_icon/card_sunflower.png"),
            POTATO_MINE, loadByPath("src/main/resources/component/plant_card_icon/card_potato_mine.png")
    ));

    private final static int CARD_WIDTH = 65;
    private final static int CARD_HEIGHT = 90;

    private final static Color HOVER_BG_COLOR = new Color(0, 0, 0, 180);
    private final static Color TEXT_COLOR = Color.WHITE;
    private final static Color SELECTED_COLOR = Color.YELLOW;
    private final static Font INFO_FONT = new Font("SansSerif", Font.BOLD, 12);

    private final BufferedImage icon;

    private boolean selected = false;
    private boolean affordable = true;
    private boolean hovered = false;

    public PlantCardComponent(PlantInfo plantInfo) {
        super(plantInfo);

        setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        icon = iconLoader.get(plantInfo);

        if (icon == null) {
            throw new IllegalArgumentException("No icon found for " + plantInfo);
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setHovered(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setHovered(false);
            }
        });
    }

    private void setHovered(boolean hovered) {
        if (this.hovered != hovered) {
            this.hovered = hovered;
            repaint();
        }
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            repaint();
        }
    }

    public void setAffordable(boolean affordable) {
        if (this.affordable != affordable) {
            this.affordable = affordable;
            repaint();
        }
        setEnabled(affordable);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g.create();

        int offsetY = isSelected() ? -10 : 0;
        g2d.translate(0, offsetY); // сдвигаем всю отрисовку вверх

        // 1. Рисуем иконку растения
        g2d.drawImage(icon, 0, 0, CARD_WIDTH, CARD_HEIGHT, this);

        // 2. Если карточка недоступна - затемняем
        if (isNotAffordable()) {
            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);
        }

        // 3. Если карточка выбрана - рисуем выделение
        if (isSelected()) {
            g2d.setColor(SELECTED_COLOR);
            g2d.drawRect(0, 0, CARD_WIDTH - 1, CARD_HEIGHT - 1);
            g2d.drawRect(1, 1, CARD_WIDTH - 3, CARD_HEIGHT - 3);
        }

        // 4. Если курсор над карточкой - показываем инфу
        if (isHovered()) {
            g2d.setColor(HOVER_BG_COLOR);
            g2d.fillRect(0, 0, CARD_WIDTH, CARD_HEIGHT);

            g2d.setColor(TEXT_COLOR);
            g2d.setFont(INFO_FONT);

            FontMetrics metrics = g2d.getFontMetrics();
            String name = getPlantInfo().getName();
            int x = (CARD_WIDTH - metrics.stringWidth(name)) / 2;
            int y = (CARD_HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();
            g2d.drawString(name, x, y);

            String price = "Цена: " + getPlantInfo().getPrice();
            int priceY = y + metrics.getHeight() + 5;
            int priceX = (CARD_WIDTH - metrics.stringWidth(price)) / 2;
            g2d.drawString(price, priceX, priceY);
        }

        g2d.dispose();
    }

    public boolean isHovered() {
        return hovered;
    }

    public boolean isNotHovered() {
        return !isHovered();
    }

    public boolean isAffordable() {
        return affordable;
    }

    public boolean isNotAffordable() {
        return !isAffordable();
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isNotSelected() {
        return !isSelected();
    }

    public PlantInfo getPlantInfo() {
        return model();
    }
}
