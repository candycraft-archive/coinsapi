package de.pauhull.coins.bungee.command;

import de.pauhull.coins.api.CoinAPI;
import de.pauhull.coins.common.Messages;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class CoinsCommand extends Command {

    public CoinsCommand(Plugin plugin) {
        super("coins");
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, this);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if (args.length < 1) {
            if (!(sender instanceof ProxiedPlayer)) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§cDu musst ein Spieler sein, um diesen Command zu benutzen"));
                return;
            }

            ProxiedPlayer player = (ProxiedPlayer) sender;

            CoinAPI.getInstance().getCoins(player.getUniqueId(), coins -> {

                DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);
                String number = format.format(coins);

                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Du hast §e" + number + "§7 Coin" + (coins != 1 ? "s" : "") + "."));
            });

            return;
        }

        if (args.length == 1) {
            if (!sender.hasPermission("coins.seeother")) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS));
                return;
            }

            CoinAPI.getInstance().getCoins(args[0], coins -> {

                DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);
                String number = format.format(coins);

                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§e" + args[0] + "§7 hat §e" + number + "§7 Coin" + (coins != 1 ? "s" : "") + "."));
            });

            return;
        }

        if (args.length == 3) {
            if (!sender.hasPermission("coins.edit")) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.NO_PERMISSIONS));
                return;
            }

            int coins;
            try {
                coins = Integer.valueOf(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§c/coins <set|add|remove> <Coins>"));
                return;
            }

            DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN);
            String number = format.format(coins);

            if (args[0].equalsIgnoreCase("set")) {
                CoinAPI.getInstance().setCoins(args[1], coins);
                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§e" + args[1] + "§7 hat nun §e" + number + "§7 Coin" + (coins != 1 ? "s" : "") + "."));
            } else if (args[0].equalsIgnoreCase("add")) {
                CoinAPI.getInstance().addCoins(args[1], coins);
                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§e" + number + "§7 Coin" + (coins != 1 ? "s" : "") + " wurden §e" + args[1] + "§7 hinzugefügt."));
            } else if (args[0].equalsIgnoreCase("remove")) {
                CoinAPI.getInstance().removeCoins(args[1], coins);
                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§e" + number + "§7 Coin" + (coins != 1 ? "s" : "") + " wurden §e" + args[1] + "§7 entfernt."));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "§c/coins <set|add|remove> <Coins>"));
            }

            return;
        }

        sender.sendMessage(TextComponent.fromLegacyText(Messages.PREFIX + "Dieser §cBefehl§7 wurde nicht gefunden."));
    }

}