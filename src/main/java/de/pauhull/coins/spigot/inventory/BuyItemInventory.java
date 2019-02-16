package de.pauhull.coins.spigot.inventory;

import de.pauhull.coins.api.CoinAPI;
import de.pauhull.coins.spigot.buyable.SpigotBuyable;
import de.pauhull.coins.spigot.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class BuyItemInventory implements Listener {

    private static final String TITLE = "§c kaufen?";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(" ").build();
    private static final ItemStack BUY = new ItemBuilder(Material.EMERALD_BLOCK).setDisplayName("§a§lKAUFEN").build();
    private static final ItemStack CANCEL = new ItemBuilder(Material.REDSTONE_BLOCK).setDisplayName("§c§lABBRECHEN").build();
    private static final ItemStack WAIT = new ItemBuilder(Material.IRON_BLOCK).setDisplayName("§7§lKAUF WIRD VERARBEITET...").build();

    @Getter
    private List<SpigotBuyable> items = new ArrayList<>();

    public void register(SpigotBuyable... buyables) {
        items.addAll(Arrays.asList(buyables));
    }

    public void show(Player player, SpigotBuyable item) {

        String itemName = item.getName();
        if (itemName.length() + TITLE.length() > 32) {
            itemName = "§cDieses Item";
        }

        Inventory inventory = Bukkit.createInventory(null, 27, itemName + TITLE);

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_GLASS);
        }

        ItemMeta meta = BUY.getItemMeta();
        meta.setLore(Arrays.asList(" ", "§8» §eKosten: §7" + NumberFormat.getInstance(Locale.GERMAN).format(item.getCost()), " "));
        BUY.setItemMeta(meta);

        inventory.setItem(11, BUY);
        inventory.setItem(13, item.getItem());
        inventory.setItem(15, CANCEL);

        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        player.openInventory(inventory);
    }

    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().endsWith(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        SpigotBuyable foundItem = null;

        for (SpigotBuyable checkedItem : items) {
            if (inventory.getItem(13).equals(checkedItem.getItem())) {
                foundItem = checkedItem;
                break;
            }
        }

        if (foundItem == null)
            return;

        final SpigotBuyable buyable = foundItem;

        if (stack != null) {
            if (stack.equals(CANCEL)) {
                buyable.onCancel(player);
            } else if (stack.getType() == BUY.getType()) {
                inventory.setItem(event.getSlot(), WAIT);
                player.updateInventory();

                buyable.hasBought(player, hasBought -> {
                    if (hasBought) {
                        buyable.onAlreadyBought(player);
                    } else {
                        CoinAPI.getInstance().hasEnoughCoins(player.getUniqueId(), buyable.getCost(), enoughCoins -> {
                            if (enoughCoins) {
                                CoinAPI.getInstance().removeCoins(player.getUniqueId(), buyable.getCost());
                                buyable.onBuy(player);
                            } else {
                                buyable.onNotEnoughCoins(player);
                            }
                        });
                    }
                });
            }
        }
    }

}
