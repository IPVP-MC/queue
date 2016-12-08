package org.ipvp.queue.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.ipvp.queue.Priority;
import org.ipvp.queue.QueuePlugin;

public class PlayerListener implements Listener {

    private QueuePlugin plugin;

    public PlayerListener(QueuePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Priority priority = plugin.getPriority(player);
        // TODO: Priority, and check when player joins queue or leaves queue
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Priority");
        out.writeUTF(player.getUniqueId().toString());
        out.writeUTF(priority.getName());
        out.writeInt(priority.getWeight());
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> player.sendPluginMessage(plugin, "Queue", out.toByteArray()), 5L); // Let player initialize first
    }
}
