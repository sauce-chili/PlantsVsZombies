package vstu.oop.model.core.player;

class SunTokenWallet {
    private int sunTokens;
    private final int maxSunTokens;
    private SunTokensChangeListener onSunTokensCountChanged;

    SunTokenWallet() {
        this(0);
    }

    SunTokenWallet(int initialSunTokens) {
        this(initialSunTokens, 9999);
    }

    SunTokenWallet(int initialSunTokens, int maxSunTokensCapacity) {
        if (initialSunTokens < 0) {
            throw new IllegalArgumentException("Gold count cannot be negative");
        }

        if (maxSunTokensCapacity < 0) {
            throw new IllegalArgumentException("Sun tokens capacity cannot be negative");
        }

        if (initialSunTokens > maxSunTokensCapacity) {
            throw new IllegalArgumentException("Sun tokens cannot be greater than max sun tokens capacity");
        }

        this.sunTokens = initialSunTokens;
        this.maxSunTokens = maxSunTokensCapacity;
    }

    void accrueSunTokens(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Credit count cannot be negative");
        }

        if (sunTokens + amount > maxSunTokens) {
            amount = maxSunTokens - sunTokens;
        }

        int was = this.sunTokens;
        this.sunTokens += amount;

        if (onSunTokensCountChanged != null && was != sunTokens) {
            onSunTokensCountChanged.onSunTokensCountChanged(was, sunTokens);
        }
    }

    void deductSunTokens(int amount) {
        if (sunTokens < amount) {
            throw new IllegalArgumentException("Not enough gold");
        }

        if (amount < 0) {
            throw new IllegalArgumentException("Credit count cannot be negative");
        }

        int was = this.sunTokens;
        this.sunTokens = Math.max(this.sunTokens - amount, 0);

        if (onSunTokensCountChanged != null && was != sunTokens) {
            onSunTokensCountChanged.onSunTokensCountChanged(was, sunTokens);
        }
    }

    int getSunTokens() {
        return this.sunTokens;
    }

    void setOnSunTokensCountChanged(SunTokensChangeListener onSunTokensCountChanged) {
        this.onSunTokensCountChanged = onSunTokensCountChanged;
    }

    interface SunTokensChangeListener {
        void onSunTokensCountChanged(int was, int became);
    }
}
