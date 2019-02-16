package de.pauhull.coins.common.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class CoinsTable {

    private static final String TABLE = "coins";

    private MySQL mySQL;
    private ExecutorService executorService;

    public CoinsTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `coins` INT, PRIMARY KEY (`id`))");
    }

    public void exists(UUID uuid, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {
                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid + "'");

                if (result.next()) {
                    consumer.accept(result.getString("uuid") != null);
                    return;
                }

                consumer.accept(false);
            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void create(UUID uuid) {
        exists(uuid, exists -> {
            if (!exists) {
                mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', 0)");
            }
        });
    }

    public void getCoins(UUID uuid, Consumer<Integer> consumer) {
        exists(uuid, exists -> {
            if (exists) {
                try {
                    ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");

                    if (result.next()) {
                        consumer.accept(result.getInt("coins"));
                        return;
                    }

                    consumer.accept(0);

                } catch (SQLException e) {
                    e.printStackTrace();
                    consumer.accept(0);
                }
            } else {
                consumer.accept(0);
            }
        });
    }

    public void setCoins(UUID uuid, int coins) {
        exists(uuid, exists -> {
            if (exists) {
                mySQL.update("UPDATE `" + TABLE + "` SET `coins`=" + coins + " WHERE `uuid`='" + uuid.toString() + "'");
            } else {
                create(uuid);
                setCoins(uuid, coins);
            }
        });
    }

    public void hasEnoughCoins(UUID uuid, int coins, Consumer<Boolean> consumer) {
        exists(uuid, exists -> {
            if (exists) {
                getCoins(uuid, currentCoins -> consumer.accept(currentCoins >= coins));
            } else {
                create(uuid);
                hasEnoughCoins(uuid, coins, consumer);
            }
        });
    }

}
