package de.godtitan.coins;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 10.12.2018
 *
 * @author pauhull
 * @deprecated Use {@link de.pauhull.coins.api.CoinAPI} instead
 */
@Deprecated
public class CoinAPI {

    @Deprecated
    public static CoinAPI getInstance() {
        return new CoinAPI();
    }

    @Deprecated
    public void exists(String name, Consumer<Boolean> consumer) {
        de.pauhull.coins.api.CoinAPI.getInstance().exists(name, consumer);
    }

    @Deprecated
    public void exists(UUID uuid, Consumer<Boolean> consumer) {
        de.pauhull.coins.api.CoinAPI.getInstance().exists(uuid, consumer);
    }

    @Deprecated
    public void create(String name) {
        de.pauhull.coins.api.CoinAPI.getInstance().create(name);
    }

    @Deprecated
    public void create(UUID uuid) {
        de.pauhull.coins.api.CoinAPI.getInstance().create(uuid);
    }

    @Deprecated
    public void getCoins(String name, Consumer<Integer> consumer) {
        de.pauhull.coins.api.CoinAPI.getInstance().getCoins(name, consumer);
    }

    @Deprecated
    public void getCoins(UUID uuid, Consumer<Integer> consumer) {
        de.pauhull.coins.api.CoinAPI.getInstance().getCoins(uuid, consumer);
    }

    @Deprecated
    public void setCoins(String name, int coins) {
        de.pauhull.coins.api.CoinAPI.getInstance().setCoins(name, coins);
    }

    @Deprecated
    public void setCoins(UUID uuid, int coins) {
        de.pauhull.coins.api.CoinAPI.getInstance().setCoins(uuid, coins);
    }

    @Deprecated
    public void addCoins(String name, int coins) {
        de.pauhull.coins.api.CoinAPI.getInstance().addCoins(name, coins);
    }

    @Deprecated
    public void addCoins(UUID uuid, int coins) {
        de.pauhull.coins.api.CoinAPI.getInstance().addCoins(uuid, coins);
    }

    @Deprecated
    public void removeCoins(String name, int coins) {
        de.pauhull.coins.api.CoinAPI.getInstance().removeCoins(name, coins);
    }

    @Deprecated
    public void removeCoins(UUID uuid, int coins) {
        de.pauhull.coins.api.CoinAPI.getInstance().removeCoins(uuid, coins);
    }

    @Deprecated
    public void hasEnoughCoins(String name, int coins, Consumer<Boolean> consumer) {
        de.pauhull.coins.api.CoinAPI.getInstance().hasEnoughCoins(name, coins, consumer);
    }

    @Deprecated
    public void hasEnoughCoins(UUID uuid, int coins, Consumer<Boolean> consumer) {
        de.pauhull.coins.api.CoinAPI.getInstance().hasEnoughCoins(uuid, coins, consumer);
    }

}
