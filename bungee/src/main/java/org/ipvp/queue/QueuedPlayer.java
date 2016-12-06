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

    public ProxiedPlayer getHandle() {
        return handle;
    }

    public Priority getPriority() {
        return priority;
    }

    public Queue getQueue() {
        return queue;
    }

    public boolean isInQueue() {
        return queue != null;
    }

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
