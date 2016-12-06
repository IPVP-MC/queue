package org.ipvp.queue.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.ipvp.queue.Priority;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.sign.InfoSign;
import org.ipvp.queue.sign.JoinSign;

public class BungeeListener implements PluginMessageListener {

    private QueuePlugin plugin;

    public BungeeListener(QueuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("Queue")) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();

        if (subchannel.equals("Players")) {
            String server = in.readUTF();
            int players = in.readInt();
            int totalPlayers = in.readInt();
            plugin.getSigns(server).stream()
                    .filter(s -> s instanceof JoinSign)
                    .map(s -> (JoinSign) s)
                    .forEach(sign -> {
                        sign.update(players, totalPlayers);
                    });
        } else if (subchannel.equalsIgnoreCase("Counts")) { // Queued player rank counts
            String server = in.readUTF();
            String rank = in.readUTF();
            int count = in.readInt();
            Priority priority = plugin.getPriority(rank);
            plugin.getSigns(server).stream()
                    .filter(s -> s instanceof InfoSign)
                    .map(s -> (InfoSign) s)
                    .forEach(sign -> {
                        sign.update(priority, count);
                    });
        }
    }
}
