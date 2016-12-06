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
        // TODO: Need to send rank information for each queue
        plugin.getQueues().forEach(queue -> {
            Map<String, Integer> numRanksInQueue = new HashMap<>();
            queue.stream().map(QueuedPlayer::getPriority).forEach(p -> {
                int num = numRanksInQueue.getOrDefault(p.getName(), 0);
                numRanksInQueue.put(p.getName(), num + 1);
            });
            ServerInfo server = queue.getTarget();
            numRanksInQueue.forEach((name, count) -> {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Count");
                out.writeUTF(server.getName());
                out.writeUTF(name);
                out.writeInt(count);
                server.sendData("Queue", out.toByteArray());
            });
        });
    }
}
