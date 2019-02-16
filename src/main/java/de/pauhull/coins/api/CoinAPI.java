package de.pauhull.coins.api;

import lombok.Getter;

import java.util.UUID;
import java.util.function.Consumer;

public abstract class CoinAPI {

    @Getter
    private static CoinAPI instance;

    public CoinAPI() {
        instance = this;
    }

    public abstract boolean isLoaded();

    public abstract void exists(String name, Consumer<Boolean> consumer) throws APINotLoadedException;

    public abstract void exists(UUID uuid, Consumer<Boolean> consumer) throws APINotLoadedException;

    public abstract void create(String name) throws APINotLoadedException;

    public abstract void create(UUID uuid) throws APINotLoadedException;

    public abstract void getCoins(String name, Consumer<Integer> consumer) throws APINotLoadedException;

    public abstract void getCoins(UUID uuid, Consumer<Integer> consumer) throws APINotLoadedException;

    public abstract void setCoins(String name, int coins) throws APINotLoadedException;

    public abstract void setCoins(UUID uuid, int coins) throws APINotLoadedException;

    public abstract void addCoins(String name, int coins) throws APINotLoadedException;

    public abstract void addCoins(UUID uuid, int coins) throws APINotLoadedException;

    public abstract void removeCoins(String name, int coins) throws APINotLoadedException;

    public abstract void removeCoins(UUID uuid, int coins) throws APINotLoadedException;

    public abstract void hasEnoughCoins(String name, int coins, Consumer<Boolean> consumer) throws APINotLoadedException;

    public abstract void hasEnoughCoins(UUID uuid, int coins, Consumer<Boolean> consumer) throws APINotLoadedException;

    public abstract void printToConsole(String message);

    public class APINotLoadedException extends RuntimeException {

        public APINotLoadedException() {
            super("Couldn't execute query, because API is not loaded. Is the MySQL database setup correctly?");
        }

    }

}
