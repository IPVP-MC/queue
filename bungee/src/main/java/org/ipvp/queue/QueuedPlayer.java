package org.ipvp.queue;

import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Objects;

public final class QueuedPlayer {

    private ProxiedPlayer handle;
    private Priority priority;
    private Queue queue;

    public QueuedPlayer(ProxiedPlayer handle, Priority priority) {
        Objects.requireNonNull(handle, "Player cannot be null");
        this.handle = handle;
        this.priority = priority;
    }

    /**
     * Returns the ProxiedPlayer represented by this instance
     *
     * @return ProxiedPlayer handle
     */
    public ProxiedPlayer getHandle() {
        return handle;
    }

    /**
     * Returns the priority rank information about this player
     *
     * @return Priority information
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Returns the current queue this player is in
     *
     * @return Queue entered, or null
     */
    public Queue getQueue() {
        return queue;
    }

    /**
     * Returns whether or not the player is in a queue
     *
     * @return True if the player is in a queue, false otherwise
     */
    public boolean isInQueue() {
        return queue != null;
    }

    /**
     * Returns the current position inside the queue the player is waiting for
     *
     * @return Queue position
     * @throws IllegalStateException When the player is not in a queue
     */
    public int getPosition() {
        if (!isInQueue()) {
            throw new IllegalStateException("Player is not queued");
        }
        for (int i = 0 ; i < queue.size() ; i++) {
            if (queue.get(i).equals(this)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Sets the queue this player is waiting for
     *
     * @param queue New queue to wait in
     */
    public void setQueue(Queue queue) {
        this.queue = queue;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        return prime * handle.hashCode()
                + prime * priority.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof QueuedPlayer)) {
            return false;
        }
        QueuedPlayer other = (QueuedPlayer) o;
        return other.getHandle().equals(handle)
                && other.getPriority().equals(priority);
    }
}
