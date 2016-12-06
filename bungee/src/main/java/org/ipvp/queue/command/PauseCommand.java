package org.ipvp.queue.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.ipvp.queue.Queue;
import org.ipvp.queue.QueuePlugin;

public class PauseCommand extends QueuePluginCommand {

    public PauseCommand(QueuePlugin plugin) {
        super(plugin, "pausequeue", "queue.command.pause");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Usage: /pausequeue <server>"));
        } else {
            Queue queue = getPlugin().getQueue(args[0]);

            if (queue == null) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "There is no queue set up for that server"));
            } else {
                queue.setPaused(!queue.isPaused());
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + String.format("You have %s the queue " +
                        "for server %s", queue.isPaused() ? "paused" : "resumed", args[0])));
            }
        }
    }
}
