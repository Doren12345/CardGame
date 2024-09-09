package org.doren.cardgame;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.doren.cardgame.gui.GUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    public Commands() {}

    public boolean onCommand(@NotNull CommandSender sender, Command command, @NotNull String label, String[] args) {
        if (command.getName().equalsIgnoreCase("cardgame")) {

            if (args.length == 0) return commandHelp(sender, null);

            if (args[0].equalsIgnoreCase("help")) return commandHelp(sender, args);
            if (args[0].equalsIgnoreCase("battle")) return commandBattle(sender, args);
            if (args[0].equalsIgnoreCase("reload")) return commandReload(sender);
        }
        return false;
    }

    private boolean commandHelp(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.getLangData("cmd-OnlyPlayerCanExecute"));
            return true;
        }

        if (args == null || args.length < 2) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',Utils.getLangData("cmd-help")));
            Inventory inv = new GUI().generateInventoryFromConfig("battleUI");

            Player player = (Player) sender;
            player.openInventory(inv);
            return true;
        }

        // /cardgame help <cmd> args

        // /cardgame help help
        if (args[1].equalsIgnoreCase("help")) {
            sender.sendMessage(Utils.getLangData("cmd-help-help"));
            return true;
        }
        // /cardgame help reload
        if (args[1].equalsIgnoreCase("reload")) {
            sender.sendMessage(Utils.getLangData("cmd-help-help"));
            return true;
        }
        return false;
    }

    private boolean commandReload(CommandSender sender) {
        CardGame plugin = CardGame.getPlugin(CardGame.class);
        plugin.reload();
        sender.sendMessage(Utils.getLangData("cmd-reload-successful"));
        return true;
    }

    private boolean commandBattle(CommandSender sender, String[] args) {
        if (args == null || args.length < 2) {
            commandHelp(sender, new String[] {"help", "battle"});
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        return null;
    }
}
