package vstu.oop.model.entity.mob.plant;

public class ProducingPlantBehavior extends PlantBehaviorStrategy {

    private TokenSunConsumer sunConsumer;

    private Long producingTimeout;
    private Long lastTimeProducing = null;
    private int countProducingSunToken;

    public ProducingPlantBehavior(
            int countProducingSunToken,
            long producingTimeout,
            TokenSunConsumer sunConsumer
    ) {
        this.sunConsumer = sunConsumer;
        this.countProducingSunToken = countProducingSunToken;
        this.producingTimeout = producingTimeout;
    }

    @Override
    protected void behave(long currentTick) {
        boolean wasNotProducingBefore = getLastTimeProducing() == null;

        if (wasNotProducingBefore) {
            setLastTimeProducing(currentTick);
            return;
        }

        if (producingTimeoutExceed(currentTick)) {
            getSunConsumer().accrueSunTokens(getCountProducingSunToken());
            setLastTimeProducing(currentTick);
        }
    }

    protected boolean producingTimeoutExceed(long currentTick) {
        return currentTick - lastTimeProducing <= producingTimeout;
    }

    protected Long getProducingTimeout() {
        return producingTimeout;
    }

    protected void setProducingTimeout(Long producingTimeout) {
        this.producingTimeout = producingTimeout;
    }

    protected Long getLastTimeProducing() {
        return lastTimeProducing;
    }

    protected void setLastTimeProducing(Long lastTimeProducing) {
        this.lastTimeProducing = lastTimeProducing;
    }

    protected int getCountProducingSunToken() {
        return countProducingSunToken;
    }

    protected void setCountProducingSunToken(int countProducingSunToken) {
        this.countProducingSunToken = countProducingSunToken;
    }

    protected TokenSunConsumer getSunConsumer() {
        return sunConsumer;
    }

    protected void setSunConsumer(TokenSunConsumer sunConsumer) {
        this.sunConsumer = sunConsumer;
    }

    public interface TokenSunConsumer {
        void accrueSunTokens(int amount);
    }
}
