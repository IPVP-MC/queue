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

    private final ServerInfo target;
    private int maxPlayers;
    private boolean paused;
    private long lastSentTime;
    private Map<String, Integer> priorityCounts = new HashMap<>(); // TODO: Concurrent?

    public Queue(final ServerInfo target, int maxPlayers) {
        Objects.requireNonNull(target);
        this.target = target;
        this.maxPlayers = maxPlayers;
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
     * Sets the maximum amount of players able to join the target server. This
     * value should be in-sync with what the server actually accepts as a maximum value,
     * otherwise, the queue will attempt to (and likely fail) to send players to
     * the target server.
     *
     * @param maxPlayers Maximum players allowed on target server
     */
    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
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
     * Returns a unmodifiable copy of the priority counts held by this queue.
     *
     * @return Priority counts for this queue
     */
    public Map<String, Integer> getPriorityCounts() {
        return Collections.unmodifiableMap(priorityCounts);
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
                && target.getPlayers().size() < maxPlayers
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
        for (int i = 0 ; i < size() ; i++) {
            if (weight > get(i).getPriority().getWeight()) {
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
        next.getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "Sending you to " + target.getName() + "..."));
        next.getHandle().connect(target, (result, error) -> {
            // What do we do if they can't connect?
            if (result) {
                String priority = next.getPriority().getName();
                setPriorityCount(priority, getPriorityCount(priority) - 1);
                next.getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.GREEN + "You have been sent to " + target.getName()));
                lastSentTime = System.currentTimeMillis();
            } else {
                next.getHandle().sendMessage(TextComponent.fromLegacyText(ChatColor.RED + "Unable to connect to " + target.getName() + ". You were removed from the queue."));
            }
        });
    }

    /**
     * Sets the number of players of a specified priority inside this queue
     *
     * @param name Name of the priority rank
     * @param value New amount of players in this queue for the priority
     */
    public void setPriorityCount(String name, int value) {
        priorityCounts.put(name, value);
    }

    /**
     * Returns the number of players of a specified priority inside this queue
     *
     * @param name Name of the priority rank
     * @return Number of players in this queue with matching priority
     */
    public int getPriorityCount(String name) {
        return priorityCounts.getOrDefault(name, 0);
    }
}
