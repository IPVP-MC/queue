package org.ipvp.queue;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.ipvp.queue.command.LeaveCommand;
import org.ipvp.queue.command.PauseCommand;
import org.ipvp.queue.command.QueueCommand;
import org.ipvp.queue.command.SetLimitCommand;
import org.ipvp.queue.task.PositionNotificationTask;
import org.ipvp.queue.task.SendPlayerCountsTask;
import org.ipvp.queue.task.SendQueueInformationTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static net.md_5.bungee.api.ChatColor.GREEN;
import static net.md_5.bungee.api.ChatColor.YELLOW;

public class QueuePlugin extends Plugin implements Listener {

    private Map<String, Integer> maxPlayers = new HashMap<>();
    private Map<String, Queue> queues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private Map<ProxiedPlayer, QueuedPlayer> queuedPlayers = new ConcurrentHashMap<>();

    @Override
    public void onEnable() {
        getProxy().getServers().values().forEach(this::setupServer);
        getProxy().registerChannel("Queue");
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().getScheduler().schedule(this, () -> {
            getQueues().stream().filter(Queue::canSend).forEach(Queue::sendNext);
        }, 50, 50, TimeUnit.MILLISECONDS);
        getProxy().getScheduler().schedule(this, new SendQueueInformationTask(this), 5, 5, TimeUnit.SECONDS);
        getProxy().getScheduler().schedule(this, new SendPlayerCountsTask(this), 5, 5, TimeUnit.SECONDS);
        getProxy().getScheduler().schedule(this, new PositionNotificationTask(this), 1, 1, TimeUnit.MINUTES);
        getProxy().getPluginManager().registerCommand(this, new LeaveCommand(this));
        getProxy().getPluginManager().registerCommand(this, new PauseCommand(this));
        getProxy().getPluginManager().registerCommand(this, new QueueCommand(this));
        getProxy().getPluginManager().registerCommand(this, new SetLimitCommand(this));
    }

    // Gets the max players for a server and caches it for later use
    private void setupServer(ServerInfo info) {
        final String name = info.getName();
        info.ping((p, err) -> {
            if (p == null || p.getPlayers() == null) {
                return;
            }
            int max = p.getPlayers().getMax();
            maxPlayers.put(name, max);
            if (!queues.containsKey(name)) {
                queues.put(name, new Queue(info, max));
            } else {
                Queue queue = queues.get(name);
                queue.setMaxPlayers(max);
            }
        });
    }

    /**
     * Returns all current queues for target servers
     *
     * @return Loaded queues for all servers
     */
    public Collection<Queue> getQueues() {
        return queues.values();
    }

    /**
     * Returns a Queue for a specified server
     *
     * @param server Server to check
     * @return Queue for the server
     */
    public Queue getQueue(String server) {
        return queues.get(server);
    }

    /**
     * Returns a players QueuedPlayer wrapper
     *
     * @param player Player to find
     * @return QueuedPlayer wrapper
     */
    public QueuedPlayer getQueued(ProxiedPlayer player) {
        return queuedPlayers.get(player);
    }

    /**
     * Registers a queued player
     *
     * @param player Player to register
     */
    public void addQueued(QueuedPlayer player) {
        queuedPlayers.put(player.getHandle(), player);
    }

    /**
     * Returns all QueuedPlayers
     *
     * @return All QueuedPlayer wrappers
     */
    public Collection<QueuedPlayer> getQueued() {
        return queuedPlayers.values();
    }

    /**
     * Gets the maximum players allowed on a server in this bungee instance
     *
     * @param server the name of the server
     * @return the cached maximum amount of players allowed on the server. will return -1
     * and make a call to load the data when the server exists but the data has not yet
     * been loaded.
     * @throws IllegalArgumentException if a server with the name does not exist
     */
    public int getMaxPlayers(String server) {
        if (!maxPlayers.containsKey(server)) {
            ServerInfo info = getProxy().getServerInfo(server);

            if (info == null) {
                throw new IllegalArgumentException("A server with that name does not exist");
            }

            this.setupServer(info);
            return -1;
        }

        return maxPlayers.get(server);
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        String channel = event.getTag();

        System.out.print("Found message on channel: " + event.getTag());

        if (channel.equals("Queue")) {
            String sub = in.readUTF();
            UUID uuid = UUID.fromString(in.readUTF());
            ProxiedPlayer player = getProxy().getPlayer(uuid);

            System.out.print("Message on subchannel: " + sub);

            if (player == null) {
                System.out.print("Player is null? UUID: " + uuid);
                return;
            }

            if (sub.equals("Join")) {
                System.out.print("In Join");
                QueuedPlayer queued = getQueued(player);
                String target = in.readUTF();
                int weight = queued.getPriority().getWeight();
                Queue queue = getQueue(target);

                if (queue == null) {
                    System.out.print("Queue is null");
                    ServerInfo server = getProxy().getServerInfo(target); // Find server
                    if (server == null) {
                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Invalid server provided"));
                        System.out.print("Invalid server");
                        return;
                    } else {
                        queue = new Queue(server, getMaxPlayers(server.getName()));
                        queues.put(server.getName(), queue);
                        System.out.print("Made new queue");
                    }
                }

                if (queued.getPriority().isBypass()) {
                    ServerInfo info = getProxy().getServerInfo(target);
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Sending you to " + info.getName() + "..."));
                    player.connect(info, (result, error) -> {
                        if (result) {
                            player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "You have been sent to " + info.getName()));
                        }
                    });
                    return;
                }

                if (queued.getQueue() != null) {
                    if (queued.getQueue().getTarget().getName().equalsIgnoreCase(target)) {
                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "You are already queued for this server"));
                    } else {
                        player.sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Use /leavequeue to leave your current queue."));
                    }
                    System.out.print("/leavequeue");
                    return;
                }

                int index = queue.getIndexFor(weight);
                queue.add(index, queued);
                queued.setQueue(queue);
                String priority = queued.getPriority().getName();
                queue.setPriorityCount(priority, queue.getPriorityCount(priority) + 1);
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "You have joined the queue for " + queue.getTarget().getName()));
                player.sendMessage(TextComponent.fromLegacyText(String.format(YELLOW + "You are currently in position "
                        + GREEN + "%d " + YELLOW + "of " + GREEN + "%d", queued.getPosition(), queue.size())));
            } else if (sub.equals("Priority")) {
                String name = in.readUTF();
                int weight = in.readInt();
                queuedPlayers.put(player, new QueuedPlayer(player, new Priority(name, weight)));
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerDisconnectEvent event) {
        handleLeave(event.getPlayer());
    }

    @EventHandler
    public void onSwitchServer(ServerSwitchEvent event) {
        handleLeave(event.getPlayer());
    }

    private void handleLeave(ProxiedPlayer player) {
        QueuedPlayer queued = queuedPlayers.get(player);
        if (queued != null && queued.getQueue() != null) {
            Queue queue = queued.getQueue();
            String priority = queued.getPriority().getName();
            queue.remove(queued);
            queued.setQueue(null);
            queue.setPriorityCount(priority, queue.getPriorityCount(priority) - 1);
        }
    }
}
