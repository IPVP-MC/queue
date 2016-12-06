package org.ipvp.queue;

import net.md_5.bungee.api.config.ServerInfo;

import java.util.LinkedList;
import java.util.Objects;

public class Queue extends LinkedList<QueuedPlayer> {

    private long lastSentTime;
    private boolean paused;
    private ServerInfo target;
    private int maxPlayers;

    public Queue(ServerInfo target, int maxPlayers) {
        Objects.requireNonNull(target);
        this.target = target;
        this.maxPlayers = maxPlayers;
    }

    public ServerInfo getTarget() {
        return target;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean canSend() {
        return !isPaused() && !isEmpty()
                && target.getPlayers().size() < maxPlayers
                && lastSentTime + 250L < System.currentTimeMillis();
    }

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

    public void sendNext() {
        if (!canSend()) {
            throw new IllegalStateException("Cannot send next player in queue");
        }

        QueuedPlayer next = get(0);
        next.getHandle().connect(target, (result, error) -> {
            // What do we do if they can't connect?

        });
    }
}
