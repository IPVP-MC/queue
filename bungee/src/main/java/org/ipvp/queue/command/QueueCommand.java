package org.ipvp.queue.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.ipvp.queue.Queue;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.QueuedPlayer;

public class QueueCommand extends QueuePluginCommand {

    public QueueCommand(QueuePlugin plugin) {
        super(plugin, "queue");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(TextComponent.fromLegacyText("You must be a player to use this command"));
        } else {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            QueuedPlayer queuedPlayer = getPlugin().getQueued(player);
            Queue queue = queuedPlayer.getQueue();

            if (queue == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are not in a queue"));
            } else {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + String.format("You are currently in " +
                        "position %d of %d for server %s", queuedPlayer.getPosition(), queue.size(), queue.getTarget().getName())));
            }
        }
    }
}
