package org.ipvp.queue.task;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.config.ServerInfo;
import org.ipvp.queue.QueuePlugin;
import org.ipvp.queue.QueuedPlayer;

import java.util.HashMap;
import java.util.Map;

public class SendQueueInformationTask implements Runnable {

    private QueuePlugin plugin;

    public SendQueueInformationTask(QueuePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.getQueues().forEach(queue -> {
            Map<String, Integer> priorityCount = queue.getPriorityCounts();

            priorityCount.forEach((name, count) -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Counts");
                out.writeUTF(queue.getTarget().getName());
                out.writeUTF(name);
                out.writeInt(count);
                byte[] data = out.toByteArray();

                for (ServerInfo server : plugin.getProxy().getServers().values()) {
                    if (server.getPlayers().isEmpty()) {
                        continue;
                    }

                    byte[] copy = new byte[data.length];
                    System.arraycopy(data, 0, copy, 0, data.length);
                    server.sendData("Queue", copy);
                }
            });
        });
    }
}
