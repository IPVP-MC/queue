package org.ipvp.queue;

import java.util.Objects;

/**
 * Represents a server rank and the weight they carry in the priority queue
 */
public final class Priority {

    public static final Priority DEFAULT_PRIORITY = new Priority("Default", "queue.priority.default", 0); // TODO: plugin.yml default

    private String name;
    private String permission;
    private int weight;

    public Priority(String name, String permission, int weight) {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(permission, "Permission cannot be null");
        this.name = name;
        this.permission = permission;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public int getWeight() {
        return weight;
    }
}
