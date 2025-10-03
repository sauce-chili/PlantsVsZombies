package vstu.oop.model.core.player;

import vstu.oop.model.core.world.Cell;
import vstu.oop.model.entity.mob.plant.Plant;
import vstu.oop.model.entity.mob.plant.ProducingPlantBehavior;

import java.util.*;

import static java.util.Objects.*;

public class Player {

    private final Collection<PlayerListener> listeners = new HashSet<>();
    private final SunTokenWallet wallet;
    private PlantCatalogue catalogue;

    private Class<? extends Plant> selectedPlantToBuy;

    public Player(
            int initialCountSunTokens
    ) {
        wallet = new SunTokenWallet(initialCountSunTokens);
        wallet.setOnSunTokensCountChanged(this::onBalanceChanged);
    }

    public void setCatalogue(PlantCatalogue catalogue) {
        if (this.catalogue != null) {
            throw new IllegalStateException("The catalogue has already been set.");
        }
        this.catalogue = catalogue;
    }

    public PlantCatalogue getCatalogue() {
        return catalogue;
    }

    public ProducingPlantBehavior.TokenSunConsumer getSunTokenSunConsumer() {
        return wallet::accrueSunTokens;
    }

    public Optional<Throwable> placeSelectedPlant(Cell cell) {
        if (catalogue == null) {
            return Optional.of(new IllegalStateException("No catalogue has been set."));
        }

        if (selectedPlantAbsent()) {
            return Optional.of(new IllegalStateException("Before purchasing and planting a plant, it must be selected."));
        }

        if (cell.planted()) {
            return Optional.of(new IllegalArgumentException("Cannot place plant in already occupied cell"));
        }

        if (catalogue.hasNotPlant(selectedPlantToBuy)) {
            return Optional.of(new IllegalArgumentException("Hasn't same plant in already catalogue"));
        }

        if (catalogue.notEnoughMoneyToBuy(selectedPlantToBuy, wallet)) {
            return Optional.of(new IllegalArgumentException("Not enough money to buy"));
        }

        Plant plant = catalogue.buyPlant(selectedPlantToBuy, wallet);
        cell.setPlant(plant);

        clearSelection();

        firePlantPlaced(plant, cell);

        return Optional.empty();
    }

    private boolean selectedPlantAbsent() {
        return selectedPlantToBuy == null;
    }

    public boolean canBeBought(Class<? extends Plant> plant) {
        requireNonNull(plant);
        return nonNull(catalogue) && catalogue.hasPlant(plant) && catalogue.enoughMoneyToBuy(plant, wallet);
    }

    public boolean canNotBeBought(Class<? extends Plant> plant) {
        return !canBeBought(plant);
    }

    public Optional<Class<? extends Plant>> getSelectedPlantToBuy() {
        return Optional.ofNullable(selectedPlantToBuy);
    }

    public boolean setSelectedPlant(Class<? extends Plant> plant) {
        requireNonNull(plant);
        if (canBeBought(plant)) {
            selectedPlantToBuy = plant;
            return true;
        }
        return false;
    }

    public void clearSelection() {
        selectedPlantToBuy = null;
    }

    public int getBalance() {
        return wallet.getSunTokens();
    }

    public void subscribe(PlayerListener listener) {
        requireNonNull(listener);
        listeners.add(listener);
    }

    public void unsubscribe(PlayerListener listener) {
        requireNonNull(listener);
        listeners.remove(listener);
    }

    private void onBalanceChanged(int prevBalance, int newBalance) {
        getSelectedPlantToBuy()
                .filter(this::canNotBeBought)
                .ifPresent(ignored -> clearSelection());
        fireSunTokenBalanceChanged(prevBalance, newBalance);
    }

    private void fireSunTokenBalanceChanged(int prevBalance, int newBalance) {
        listeners.forEach(l -> l.onPlayerSunTokenBalanceChanged(prevBalance, newBalance));
    }

    private void firePlantPlaced(Plant plant, Cell cell) {
        listeners.forEach(l -> l.onPlantPlaced(plant, cell));
    }
}
