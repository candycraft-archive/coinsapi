package de.pauhull.coins.bungee;

import de.pauhull.coins.api.CoinAPI;
import de.pauhull.coins.bungee.command.CoinsCommand;
import de.pauhull.coins.common.CoinAPIThreadFactory;
import de.pauhull.coins.common.sql.CoinsTable;
import de.pauhull.coins.common.sql.MySQL;
import de.pauhull.uuidfetcher.bungee.BungeeUUIDFetcher;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

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
public class CoinAPIBungeeAdapter extends CoinAPI {

    @Getter
    private boolean loaded = false;

    private Plugin plugin;
    private MySQL mySQL;
    private File configFile;
    private Configuration config;
    private CoinsTable coinsTable;
    private ExecutorService executorService;
    private BungeeUUIDFetcher uuidFetcher;

    @Override
    public void exists(String name, Consumer<Boolean> consumer) throws CoinAPI.APINotLoadedException {
        uuidFetcher.fetchProfileAsync(name, (ignored, uuid) -> {
            if (uuid != null) {
                exists(uuid, consumer);
            } else {
                consumer.accept(false);
            }
        });
    }

    @Override
    public void exists(UUID uuid, Consumer<Boolean> consumer) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        coinsTable.exists(uuid, consumer);
    }

    @Override
    public void create(String name) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, (ignored, uuid) -> {
            if (uuid != null) {
                create(uuid);
            }
        });
    }

    @Override
    public void create(UUID uuid) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        coinsTable.create(uuid);
    }

    @Override
    public void getCoins(String name, Consumer<Integer> consumer) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, (ignored, uuid) -> {
            if (uuid != null) {
                getCoins(uuid, consumer);
            } else {
                consumer.accept(0);
            }
        });
    }

    @Override
    public void getCoins(UUID uuid, Consumer<Integer> consumer) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        coinsTable.getCoins(uuid, consumer);
    }

    @Override
    public void setCoins(String name, int coins) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, (ignored, uuid) -> {
            if (uuid != null) {
                setCoins(uuid, coins);
            }
        });
    }

    @Override
    public void setCoins(UUID uuid, int coins) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        coinsTable.setCoins(uuid, coins);
    }

    @Override
    public void addCoins(String name, int coins) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, (ignored, uuid) -> {
            if (uuid != null) {
                addCoins(uuid, coins);
            }
        });
    }

    @Override
    public void addCoins(UUID uuid, int coins) throws CoinAPI.APINotLoadedException {
        getCoins(uuid, currentCoins -> {
            setCoins(uuid, currentCoins + coins);
        });
    }

    @Override
    public void removeCoins(String name, int coins) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, (ignored, uuid) -> {
            if (uuid != null) {
                removeCoins(uuid, coins);
            }
        });
    }

    @Override
    public void removeCoins(UUID uuid, int coins) throws CoinAPI.APINotLoadedException {
        addCoins(uuid, -coins);
    }

    @Override
    public void hasEnoughCoins(String name, int coins, Consumer<Boolean> consumer) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        uuidFetcher.fetchProfileAsync(name, (ignored, uuid) -> {
            if (uuid != null) {
                hasEnoughCoins(uuid, coins, consumer);
            } else {
                consumer.accept(false);
            }
        });
    }

    @Override
    public void hasEnoughCoins(UUID uuid, int coins, Consumer<Boolean> consumer) throws CoinAPI.APINotLoadedException {
        if (!isLoaded())
            throw new CoinAPI.APINotLoadedException();

        coinsTable.hasEnoughCoins(uuid, coins, consumer);
    }

    @Override
    public void printToConsole(String message) {
        ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(message));
    }

    private Configuration copyAndLoad(String resourceName, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(plugin.getResourceAsStream(resourceName), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class BungeePlugin extends Plugin {

        private CoinAPIBungeeAdapter adapter;

        @Override
        public void onEnable() {
            adapter = new CoinAPIBungeeAdapter();
            adapter.plugin = this;
            adapter.configFile = new File(getDataFolder(), "config.yml");
            adapter.config = adapter.copyAndLoad("config.yml", adapter.configFile);

            if (adapter.config == null) {
                return;
            }

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
            adapter.uuidFetcher = new BungeeUUIDFetcher();

            new CoinsCommand(this);
        }

        @Override
        public void onDisable() {
            adapter.mySQL.close();
            adapter.executorService.shutdown();
        }

    }

}
