package de.pauhull.coins.spigot;

import de.pauhull.coins.api.CoinAPI;
import de.pauhull.coins.common.CoinAPIThreadFactory;
import de.pauhull.coins.common.sql.CoinsTable;
import de.pauhull.coins.common.sql.MySQL;
import de.pauhull.uuidfetcher.spigot.SpigotUUIDFetcher;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 10.12.2018
 *
 * @author pauhull
 */
public class CoinAPISpigotAdapter extends CoinAPI {

    @Getter
    private boolean loaded = false;

    private JavaPlugin plugin;
    private MySQL mySQL;
    private File configFile;
    private FileConfiguration config;
    private CoinsTable coinsTable;
    private ExecutorService executorService;
    private SpigotUUIDFetcher uuidFetcher;

    @Override
    public void exists(String name, Consumer<Boolean> consumer) throws APINotLoadedException {
        uuidFetcher.fetchProfileAsync(name, profile -> {
            if (profile.getUuid() != null) {
                exists(profile.getUuid(), consumer);
            } else {
                consumer.accept(false);
            }
        });
    }

    @Override
    public void exists(UUID uuid, Consumer<Boolean> consumer) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        coinsTable.exists(uuid, consumer);
    }

    @Override
    public void create(String name) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, profile -> {
            if (profile.getUuid() != null) {
                create(profile.getUuid());
            }
        });
    }

    @Override
    public void create(UUID uuid) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        coinsTable.create(uuid);
    }

    @Override
    public void getCoins(String name, Consumer<Integer> consumer) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, profile -> {
            if (profile.getUuid() != null) {
                getCoins(profile.getUuid(), consumer);
            } else {
                consumer.accept(0);
            }
        });
    }

    @Override
    public void getCoins(UUID uuid, Consumer<Integer> consumer) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        coinsTable.getCoins(uuid, consumer);
    }

    @Override
    public void setCoins(String name, int coins) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, profile -> {
            if (profile.getUuid() != null) {
                setCoins(profile.getUuid(), coins);
            }
        });
    }

    @Override
    public void setCoins(UUID uuid, int coins) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        coinsTable.setCoins(uuid, coins);
    }

    @Override
    public void addCoins(String name, int coins) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, profile -> {
            if (profile.getUuid() != null) {
                addCoins(profile.getUuid(), coins);
            }
        });
    }

    @Override
    public void addCoins(UUID uuid, int coins) throws APINotLoadedException {
        getCoins(uuid, currentCoins -> {
            setCoins(uuid, currentCoins + coins);
        });
    }

    @Override
    public void removeCoins(String name, int coins) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, profile -> {
            if (profile.getUuid() != null) {
                removeCoins(profile.getUuid(), coins);
            }
        });
    }

    @Override
    public void removeCoins(UUID uuid, int coins) throws APINotLoadedException {
        addCoins(uuid, -coins);
    }

    @Override
    public void hasEnoughCoins(String name, int coins, Consumer<Boolean> consumer) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, profile -> {
            if (profile.getUuid() != null) {
                hasEnoughCoins(profile.getUuid(), coins, consumer);
            } else {
                consumer.accept(false);
            }
        });
    }

    @Override
    public void hasEnoughCoins(UUID uuid, int coins, Consumer<Boolean> consumer) throws APINotLoadedException {
        if (!isLoaded())
            throw new APINotLoadedException();

        coinsTable.hasEnoughCoins(uuid, coins, consumer);
    }

    @Override
    public void printToConsole(String message) {
        Bukkit.getConsoleSender().sendMessage(message);
    }

    private FileConfiguration copyAndLoad(String resourceName, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(plugin.getResource(resourceName), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public static class SpigotPlugin extends JavaPlugin {

        private CoinAPISpigotAdapter adapter;

        @Override
        public void onEnable() {
            adapter = new CoinAPISpigotAdapter();
            adapter.plugin = this;

            adapter.configFile = new File(getDataFolder(), "config.yml");
            adapter.config = adapter.copyAndLoad("config.yml", adapter.configFile);
            adapter.mySQL = new MySQL(adapter.config.getString("MySQL.Host"),
                    adapter.config.getString("MySQL.Port"),
                    adapter.config.getString("MySQL.Database"),
                    adapter.config.getString("MySQL.User"),
                    adapter.config.getString("MySQL.Password"),
                    adapter.config.getBoolean("MySQL.SSL"));

            if (!adapter.mySQL.connect()) {
                return;
            }

            adapter.loaded = true;
            adapter.executorService = Executors.newSingleThreadExecutor(new CoinAPIThreadFactory("CoinAPI"));
            adapter.coinsTable = new CoinsTable(adapter.mySQL, adapter.executorService);
            adapter.uuidFetcher = SpigotUUIDFetcher.getInstance();
        }

        @Override
        public void onDisable() {
            adapter.mySQL.close();
            adapter.executorService.shutdown();
        }

    }

}