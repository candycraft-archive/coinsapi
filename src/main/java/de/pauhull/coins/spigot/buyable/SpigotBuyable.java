package de.pauhull.coins.spigot.buyable;

import de.pauhull.coins.common.buyable.Buyable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * Created by Paul
 * on 10.12.2018
 *
 * @author pauhull
 */
public interface SpigotBuyable extends Buyable {

    ItemStack getItem();

    ItemStack getItemBought();

    void onCancel(Player player);

    void onBuy(Player player);

    void onNotEnoughCoins(Player player);

    void onAlreadyBought(Player player);

    void hasBought(Player player, Consumer<Boolean> consumer);

}
