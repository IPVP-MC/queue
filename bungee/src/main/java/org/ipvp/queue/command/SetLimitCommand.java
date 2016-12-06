package org.ipvp.queue.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.ipvp.queue.Queue;
import org.ipvp.queue.QueuePlugin;

public class SetLimitCommand extends QueuePluginCommand {

    public SetLimitCommand(QueuePlugin plugin) {
        super(plugin, "setlimit", "queue.command.setlimit");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /setlimit <server> <players>"));
        } else {
            Queue queue = getPlugin().getQueue(args[0]);
            int players;

            try {
                players = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Please enter a valid number for player limit"));
                return;
            }

            queue.setMaxPlayers(players);
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Set limit of queue for server "
                    + queue.getTarget().getName() + " to " + players));
        }
    }
}
