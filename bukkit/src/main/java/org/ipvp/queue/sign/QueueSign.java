package org.ipvp.queue.sign;

import org.bukkit.block.Sign;

import java.util.Objects;

/**
 * Wraps a Bukkit sign with information about a created queue sign
 */
public abstract class QueueSign {

    private final Sign sign;
    private final String server;

    /**
     * Creates a wrapped sign for a queue
     *
     * @param handle Sign to wrap
     * @param server Target server information
     */
    public QueueSign(Sign handle, String server) {
        Objects.requireNonNull(handle, "Sign cannot be null");
        Objects.requireNonNull(server, "Server cannot be null");
        this.sign = handle;
        this.server = server;
    }

    /**
     * Returns the wrapped sign handle
     *
     * @return Sign handle
     */
    public final Sign getHandle() {
       return sign;
    }

    /**
     * Returns the target server
     *
     * @return Target server
     */
    public final String getServer() {
        return server;
    }
}
