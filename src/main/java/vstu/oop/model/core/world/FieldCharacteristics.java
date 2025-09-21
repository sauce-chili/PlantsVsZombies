package vstu.oop.model.core.world;

public record FieldCharacteristics(
        int width,
        int height,
        int rowsOfCell,
        int columnsOfCell,
        int cellWidth,
        int cellHeight,
        int plantableCellsStartingWithX,
        int plantableCellsStartingWithY
) {
    @Override
    public int columnsOfCell() {
        return columnsOfCell + 2; // 2 - count of disabled cells
    }
}
