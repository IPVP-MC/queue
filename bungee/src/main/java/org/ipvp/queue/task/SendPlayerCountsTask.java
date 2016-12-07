package org.ipvp.queue.task;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.ipvp.queue.QueuePlugin;

public class SendPlayerCountsTask implements Runnable {

    private QueuePlugin plugin;

    public SendPlayerCountsTask(QueuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getProxy().getServers().values().forEach(server -> {
            if (server.getPlayers().isEmpty()) {
                return;
            }
            plugin.getProxy().getServers().values().forEach(target -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Players");
                out.writeUTF(target.getName());
                out.writeInt(target.getPlayers().size());
                out.writeInt(plugin.getMaxPlayers(target.getName()));
                server.sendData("Queue", out.toByteArray());
            });
            System.out.print("Sent player counts to server: " + server.getName());
        });
    }
}
