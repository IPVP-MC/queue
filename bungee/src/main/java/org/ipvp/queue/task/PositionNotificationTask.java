package org.ipvp.queue.task;

import net.md_5.bungee.api.chat.TextComponent;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.QueuedPlayer;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.YELLOW;

public class PositionNotificationTask implements Runnable {

    private QueuePlugin plugin;

    public PositionNotificationTask(QueuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        System.out.print("PositionNotificationTask");
        plugin.getQueued().stream().filter(QueuedPlayer::isInQueue).forEach(p -> {
            p.getHandle().sendMessage(TextComponent.fromLegacyText(String.format(YELLOW + "You are currently in position "
                            + GREEN + "%d " + YELLOW + "of " + GREEN + "%d " + YELLOW + "for server " + GREEN + "%s",
                    p.getPosition(), p.getQueue().size(), p.getQueue().getTarget().getName())));
            p.getHandle().sendMessage(TextComponent.fromLegacyText(GREEN
                    + "Waiting too long? " + YELLOW + "Purchase a rank with priority access at " + GREEN + "store.ipvp.org"));
        });
    }
}
