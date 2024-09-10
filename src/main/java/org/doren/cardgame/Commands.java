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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("cardgame")) return false;

        if (isCommandNotFoundOrHelp(args)) return handleHelpCommand(sender, args);
        if (args[0].equalsIgnoreCase("battle")) return handleBattleCommand(sender, args);
        if (args[0].equalsIgnoreCase("reload")) return handleReloadCommand(sender);

        return false;
    }

    private boolean isCommandNotFoundOrHelp(String[] args) {
        return args.length == 0 || args[0].equalsIgnoreCase("help");
    }

    private boolean handleHelpCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.getLangData("cmd-OnlyPlayerCanExecute"));
            return true;
        }

        if (args.length < 2) {
            sendHelpMessage((Player) sender);
            return true;
        }

        String helpCommand = args[1].toLowerCase();
        if (helpCommand.equals("help") || helpCommand.equals("reload")) {
            sender.sendMessage(Utils.getLangData("cmd-help-" + helpCommand));
            return true;
        }

        return false;
    }

    private void sendHelpMessage(Player player) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Utils.getLangData("cmd-help")));
    }

    private boolean handleReloadCommand(CommandSender sender) {
        CardGame plugin = CardGame.getPlugin(CardGame.class);
        plugin.reload();
        sender.sendMessage(Utils.getLangData("cmd-reload-successful"));
        return true;
    }

    private boolean handleBattleCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return handleHelpCommand(sender, new String[] {"help", "battle"});
        }
        // 具體 battle 命令處理邏輯在此處實現
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return null;
    }
}