package org.ipvp.queue.sign;

import org.bukkit.block.Sign;

import java.util.Objects;

/**
 * Wraps a Bukkit sign with information about a created queue sign
 */
public abstract class QueueSign {

    public static enum Type {
        JOIN, INFO
    }

    private final Sign sign;
    private final Type type;
    private final String server;

    /**
     * Creates a wrapped sign for a queue
     *
     * @param handle Sign to wrap
     * @param type   Type of sign
     * @param server Target server information
     */
    public QueueSign(Sign handle, Type type, String server) {
        Objects.requireNonNull(handle, "Sign cannot be null");
        Objects.requireNonNull(type, "Type cannot be null");
        Objects.requireNonNull(server, "Server cannot be null");
        this.sign = handle;
        this.type = type;
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
     * Returns the type of sign
     *
     * @return Type of sign
     */
    public final Type getType() {
        return type;
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
