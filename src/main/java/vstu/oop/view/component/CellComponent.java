package vstu.oop.view.component;

import vstu.oop.model.core.world.Cell;
import vstu.oop.model.core.world.DisabledCell;

import java.awt.*;

public class CellComponent extends Component<Cell> {

    private boolean isHighlighted = false;

    public CellComponent(Cell cell) {
        super(cell);
        setPreferredSize(new Dimension(cell.width(), cell.height()));
        setBounds(cell.getLeftTop().x(), cell.getLeftTop().y(), cell.width(), cell.height());
    }

    public boolean isHighlighted() {
        return isHighlighted;
    }

    public boolean isNotHighlighted() {
        return !isHighlighted();
    }

    public void setHighlighting(boolean highlighted) {
        isHighlighted = highlighted;
        repaint();
    }

    public void enableHighlighting() {
        setHighlighting(true);
    }

    public void disableHighlighting() {
        setHighlighting(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Границы ячейки

        if (getModel() instanceof DisabledCell) {
            return;
        }

        g.setColor(Color.DARK_GRAY);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (isHighlighted()) {
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
            g.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        }

//        // Рисуем красную точку в центре
//        g.setColor(Color.RED);
//        int centerX = getModel().width() / 2;
//        int centerY = getModel().height() / 2;
//        int diameter = 4; // Диаметр точки
//        g.fillOval(centerX - diameter/2, centerY - diameter/2, diameter, diameter);
    }
}
