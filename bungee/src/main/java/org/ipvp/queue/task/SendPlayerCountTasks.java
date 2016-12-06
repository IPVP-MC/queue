package org.ipvp.queue.task;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.ipvp.queue.QueuePlugin;

public class SendPlayerCountTasks implements Runnable {

    private QueuePlugin plugin;

    public SendPlayerCountTasks(QueuePlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public void run() {
        plugin.getProxy().getServers().values().forEach(server -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Players");
            out.writeUTF(server.getName());
            out.writeInt(server.getPlayers().size());
            out.write(plugin.getMaxPlayers(server.getName()));
            server.sendData("Queue", out.toByteArray());
        });
    }
}
