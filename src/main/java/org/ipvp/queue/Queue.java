package org.ipvp.queue;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.*;

public class Queue extends LinkedList<QueuedPlayer> {

    /**
     * The time between ticking the queue to send a new player
     */
    public static final long TIME_BETWEEN_SENDING_MILLIS = 250L;

    private final QueuePlugin plugin;
    private final ServerInfo target;
    private boolean paused;
    private long lastSentTime;

    public Queue(QueuePlugin plugin, ServerInfo target) {
        Objects.requireNonNull(plugin);
        Objects.requireNonNull(target);
        this.plugin = plugin;
        this.target = target;
    }

    /**
     * Returns the target server for this queue.
     *
     * @return Target server
     */
    public final ServerInfo getTarget() {
        return target;
    }

    /**
     * Returns whether this queue is paused or not.
     *
     * @return True if the queue is paused, false otherwise
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Sets the paused state of this queue.
     *
     * @param paused New paused state
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Returns whether this queue can send the next player to the target server. This
     * method will only return true when the queue is not paused, has a player to send,
     * when the target server has space for the player, and if a specific interval has
     * passed since the last time a player was sent.
     *
     * @return True if the queue can send the next player, false otherwise
     */
    public boolean canSend() {
        return !isPaused() && !isEmpty()
                && target.getPlayers().size() < plugin.getMaxPlayers(target)
                && lastSentTime + TIME_BETWEEN_SENDING_MILLIS < System.currentTimeMillis();
    }

    /**
     * Searches for and returns a valid index to insert a player with a specified
     * priority weight.
     *
     * @param weight Priority weight to search for
     * @return Index to insert the priority at, returned index i will be {@code 0 <= i < {@link #size()}}
     */
    public int getIndexFor(int weight) {
        if (isEmpty() || weight == -1) {
            return 0;
        }
        for (int i = 0; i < size(); i++) {
            if (weight > get(i).getPriority()) {
                return i;
            }
        }
        return size();
    }

    /**
     * Sends the next player at index {@code 0} to the target server.
     */
    public void sendNext() {
        if (!canSend()) {
            throw new IllegalStateException("Cannot send next player in queue");
        }

        QueuedPlayer next = remove(0);
        next.setQueue(null);
        next.getHandle().sendMessage(TextComponent.fromLegacyText(
                ChatColor.GREEN + "Sending you to " + target.getName() + "..."));
        
        next.getHandle().connect(target, (result, error) -> {
            // What do we do if they can't connect?
            if (result) {
                next.getHandle().sendMessage(TextComponent.fromLegacyText(
                        ChatColor.GREEN + "You have been sent to " + target.getName()));
                lastSentTime = System.currentTimeMillis();
            } else {
                next.getHandle().sendMessage(TextComponent.fromLegacyText(
                        ChatColor.RED + "Unable to connect to " + target.getName() + ". You were removed from the queue."));
            }
        });
    }
}